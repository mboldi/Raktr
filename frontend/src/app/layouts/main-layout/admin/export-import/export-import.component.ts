import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { MatCard, MatCardContent, MatCardHeader } from '@angular/material/card';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatProgressBar } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import type { Column, Value } from 'write-excel-file/browser';
import { DeviceService } from '../../../../services/device.service';
import { CategoryService } from '../../../../services/category.service';
import { LocationService } from '../../../../services/location.service';
import { OwnerService } from '../../../../services/owner.service';
import { ScannableService } from '../../../../services/scannable.service';
import { DeviceDetails } from '../../../../model/scannable/device/deviceDetails';
import { DeviceCreateDto } from '../../../../model/scannable/device/deviceCreateDto';
import { DeviceStatus } from '../../../../model/scannable/device/deviceStatus';
import { OwnerCreateDto } from '../../../../model/owner/ownerCreateDto';
import { environment } from '../../../../../environments/environment';

interface ImportProgress {
  current: number;
  total: number;
}

interface ImportFailure {
  row: number;
  name: string;
  message: string;
}

type CellValue = string | number | boolean | Date;

function deviceColumn(
  header: string,
  width: number,
  value: (device: DeviceDetails) => Value | null | undefined,
  format?: string,
): Column<DeviceDetails> {
  return {
    header: { value: header, fontWeight: 'bold' },
    width,
    cell: (device) => {
      const cellValue = value(device);
      return cellValue === null || cellValue === undefined ? null : { value: cellValue, format };
    },
  };
}

const DEVICE_EXPORT_COLUMNS: Column<DeviceDetails>[] = [
  deviceColumn('assetTag', 16, (device) => device.assetTag),
  deviceColumn('barcode', 14, (device) => device.barcode),
  deviceColumn('name', 28, (device) => device.name),
  deviceColumn('manufacturer', 16, (device) => device.manufacturer),
  deviceColumn('model', 16, (device) => device.model),
  deviceColumn('serialNumber', 18, (device) => device.serialNumber),
  deviceColumn('quantity', 10, (device) => device.quantity),
  deviceColumn('category', 16, (device) => device.category),
  deviceColumn('location', 16, (device) => device.location),
  deviceColumn('owner', 16, (device) => device.owner?.name),
  deviceColumn('weight', 10, (device) => device.weight),
  deviceColumn('estimatedValue', 14, (device) => device.estimatedValue),
  deviceColumn('status', 16, (device) => device.status),
  deviceColumn('publicRentable', 12, (device) => device.publicRentable),
  deviceColumn('acquisitionSource', 18, (device) => device.acquisitionSource),
  deviceColumn('acquisitionDate', 16, (device) => device.acquisitionDate, 'yyyy. mm. dd.'),
  deviceColumn('warrantyEndDate', 16, (device) => device.warrantyEndDate, 'yyyy. mm. dd.'),
  deviceColumn('notes', 30, (device) => device.notes),
  deviceColumn('deleted', 10, (device) => device.deleted),
  deviceColumn('createdAt', 18, (device) => device.createdAt, 'yyyy. mm. dd. hh:mm'),
  deviceColumn('createdBy', 16, (device) => device.createdBy?.nickname),
  deviceColumn('updatedAt', 18, (device) => device.updatedAt, 'yyyy. mm. dd. hh:mm'),
  deviceColumn('updatedBy', 16, (device) => device.updatedBy?.nickname),
];

@Component({
  selector: 'app-export-import',
  imports: [
    MatCard,
    MatCardHeader,
    MatCardContent,
    MatButton,
    MatIcon,
    MatProgressSpinner,
    MatProgressBar,
  ],
  templateUrl: './export-import.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './export-import.component.scss',
})
export class ExportImportComponent {
  private deviceService = inject(DeviceService);
  private categoryService = inject(CategoryService);
  private locationService = inject(LocationService);
  private ownerService = inject(OwnerService);
  private scannableService = inject(ScannableService);
  private snackBar = inject(MatSnackBar);

  protected exportingDevices = false;

  protected importing = false;
  protected importProgress: ImportProgress | null = null;
  protected importFailures: ImportFailure[] = [];

  private nextBarcodeCounter = 0;

  protected exportDevices() {
    this.exportingDevices = true;

    this.deviceService.getDevices().subscribe({
      next: (devices) => {
        this.downloadDevicesXlsx(devices)
          .catch((error: unknown) => {
            console.error('Device export failed:', error);
            this.notifyError('Nem sikerült létrehozni az exportot!');
          })
          .finally(() => (this.exportingDevices = false));
      },
      error: (error: unknown) => {
        console.error('Failed to load devices for export:', error);
        this.exportingDevices = false;
        this.notifyError('Nem sikerült létrehozni az exportot!');
      },
    });
  }

  private async downloadDevicesXlsx(devices: DeviceDetails[]) {
    const { default: writeExcelFile } = await import('write-excel-file/browser');

    // write-excel-file only accepts plain objects for its `Object[] + columns` API (it checks
    // `value.constructor === Object`) - `DeviceDetails` instances fail that check and get
    // rejected as an invalid first argument, so they're copied into plain objects here first.
    await writeExcelFile(
      devices.map((device) => ({ ...device })),
      {
        sheet: 'Eszközök',
        columns: DEVICE_EXPORT_COLUMNS,
      },
    ).toFile(`eszkozok_${new Date().toISOString().split('T')[0]}.xlsx`);
  }

  protected onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = '';

    if (file) {
      this.importDevices(file);
    }
  }

  private async importDevices(file: File) {
    this.importing = true;
    this.importFailures = [];
    this.importProgress = null;

    let rows: Record<string, CellValue>[];
    try {
      rows = await this.parseDevicesFile(file);
    } catch {
      this.importing = false;
      this.notifyError(
        'Nem sikerült beolvasni a fájlt! Ellenőrizd, hogy valódi .xlsx fájlt választottál-e.',
      );
      return;
    }

    if (rows.length === 0) {
      this.importing = false;
      this.notifyError('A fájl nem tartalmaz importálható sorokat!');
      return;
    }

    let ownerIdByName: Map<string, number>;
    try {
      ownerIdByName = await this.ensureCategoriesLocationsOwnersExist(rows);
    } catch {
      this.importing = false;
      this.notifyError(
        'Nem sikerült létrehozni a hiányzó kategóriákat/tárolási helyeket/tulajdonosokat!',
      );
      return;
    }

    this.nextBarcodeCounter = await firstValueFrom(this.scannableService.getScannablesCount());
    this.importProgress = { current: 0, total: rows.length };

    for (let i = 0; i < rows.length; i++) {
      try {
        const dto = await this.buildDeviceCreateDto(rows[i], ownerIdByName);
        await firstValueFrom(this.deviceService.createDevice(dto));
      } catch (error) {
        this.importFailures.push({
          row: i + 2, // +1 for the header row, +1 for 1-indexing
          name:
            this.cellToString(rows[i]['name']) ||
            this.cellToString(rows[i]['assetTag']) ||
            `${i + 2}. sor`,
          message: this.extractErrorMessage(error),
        });
      }

      this.importProgress = { current: i + 1, total: rows.length };
    }

    this.importing = false;

    const successCount = rows.length - this.importFailures.length;
    this.snackBar.open(
      this.importFailures.length === 0
        ? `Import kész! Mind a(z) ${rows.length} eszköz létrejött.`
        : `Import kész: ${successCount}/${rows.length} eszköz létrejött, ${this.importFailures.length} sikertelen.`,
      'Rendben',
      {
        duration: 4000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: [this.importFailures.length === 0 ? 'success-snackbar' : 'error-snackbar'],
      },
    );
  }

  private async parseDevicesFile(file: File): Promise<Record<string, CellValue>[]> {
    const { readSheet } = await import('read-excel-file/universal');
    const [headerRow = [], ...dataRows] = (await readSheet(file)) as (CellValue | null)[][];

    const headers = headerRow.map((cell) => this.cellToString(cell));

    return dataRows
      .filter((row) => row.some((cell) => this.cellToString(cell) !== ''))
      .map((row) => {
        const record: Record<string, CellValue> = {};
        row.forEach((cell, index) => {
          const header = headers[index];
          if (header && cell !== null) {
            record[header] = cell;
          }
        });
        return record;
      });
  }

  private async ensureCategoriesLocationsOwnersExist(
    rows: Record<string, CellValue>[],
  ): Promise<Map<string, number>> {
    const [categories, locations, owners] = await Promise.all([
      firstValueFrom(this.categoryService.getCategories()),
      firstValueFrom(this.locationService.getLocations()),
      firstValueFrom(this.ownerService.getOwners()),
    ]);

    const existingCategoryNames = new Set(categories.map((category) => category.name));
    const existingLocationNames = new Set(locations.map((location) => location.name));
    const ownerIdByName = new Map(owners.map((owner) => [owner.name, owner.id]));

    const missingCategories = this.collectMissingNames(rows, 'category', existingCategoryNames);
    const missingLocations = this.collectMissingNames(rows, 'location', existingLocationNames);

    // A blank owner cell falls back to the default owner, so that name needs to exist too.
    const referencedOwnerNames = new Set(
      rows.map((row) => this.cellToString(row['owner']) || environment.defaultOwnerName),
    );
    const missingOwners = Array.from(referencedOwnerNames).filter(
      (name) => !ownerIdByName.has(name),
    );

    await Promise.all(
      missingCategories.map((name) => firstValueFrom(this.categoryService.addCategory(name))),
    );
    await Promise.all(
      missingLocations.map((name) => firstValueFrom(this.locationService.addLocation(name))),
    );

    const createdOwners = await Promise.all(
      missingOwners.map((name) =>
        firstValueFrom(this.ownerService.addOwner(new OwnerCreateDto(name, false))),
      ),
    );
    createdOwners.forEach((owner) => ownerIdByName.set(owner.name, owner.id));

    return ownerIdByName;
  }

  private collectMissingNames(
    rows: Record<string, CellValue>[],
    key: string,
    existing: Set<string>,
  ): string[] {
    const referenced = new Set(
      rows.map((row) => this.cellToString(row[key])).filter((name) => !!name),
    );

    return Array.from(referenced).filter((name) => !existing.has(name));
  }

  private async buildDeviceCreateDto(
    row: Record<string, CellValue>,
    ownerIdByName: Map<string, number>,
  ): Promise<DeviceCreateDto> {
    let assetTag = this.cellToString(row['assetTag']);
    let barcode = this.cellToString(row['barcode']);

    if (!assetTag || !barcode) {
      const generated = await this.generateNextFreeBarcode();
      if (!barcode) {
        barcode = generated;
      }
      if (!assetTag) {
        assetTag = generated;
      }
    }

    const ownerName = this.cellToString(row['owner']) || environment.defaultOwnerName;
    const status = this.cellToString(row['status']) || environment.defaultDeviceStatus;
    const acquisitionDate = this.cellToDate(row['acquisitionDate']) ?? new Date();

    return new DeviceCreateDto(
      assetTag,
      barcode,
      this.cellToString(row['name']),
      this.cellToNumber(row['weight']) as number,
      this.cellToBoolean(row['publicRentable']),
      this.cellToString(row['category']),
      this.cellToString(row['location']),
      ownerIdByName.get(ownerName) as number,
      this.cellToString(row['manufacturer']),
      this.cellToString(row['model']),
      this.cellToString(row['serialNumber']),
      this.cellToNumber(row['estimatedValue']) as number,
      status as DeviceStatus,
      this.cellToNumber(row['quantity']) ?? environment.defaultDeviceQuantity,
      this.cellToString(row['acquisitionSource']),
      acquisitionDate,
      this.cellToDate(row['warrantyEndDate']) ?? null,
      this.cellToString(row['notes']),
    );
  }

  private async generateNextFreeBarcode(): Promise<string> {
    let candidate = this.nextBarcodeCounter + 1;

    // Checked against both barcode and asset tag - a generated value might end up filling
    // either (or both) of those fields on this row, so it needs to be free in both namespaces.
    while (true) {
      const code = candidate.toString().padStart(7, '0');
      const [barcodeTaken, assetTagTaken] = await Promise.all([
        firstValueFrom(this.scannableService.isBarcodeTaken(code)),
        firstValueFrom(this.scannableService.isAssetTagTaken(code)),
      ]);

      if (!barcodeTaken && !assetTagTaken) {
        this.nextBarcodeCounter = candidate;
        return code;
      }

      candidate++;
    }
  }

  private cellToString(value: CellValue | null | undefined): string {
    if (value === null || value === undefined) {
      return '';
    }
    return String(value).trim();
  }

  private cellToNumber(value: CellValue | undefined): number | undefined {
    if (value === null || value === undefined || value === '') {
      return undefined;
    }
    const num = Number(value);
    return isNaN(num) ? undefined : num;
  }

  private cellToBoolean(value: CellValue | undefined): boolean {
    if (typeof value === 'boolean') {
      return value;
    }
    if (typeof value === 'string') {
      return value.trim().toLowerCase() === 'true';
    }
    return false;
  }

  private cellToDate(value: CellValue | undefined): Date | undefined {
    if (value instanceof Date) {
      return value;
    }
    if (typeof value === 'string' && value.trim()) {
      const parsed = new Date(value);
      return isNaN(parsed.getTime()) ? undefined : parsed;
    }
    return undefined;
  }

  private extractErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      const backendMessage = (error.error as { message?: string } | null)?.message;
      if (backendMessage) {
        return backendMessage;
      }
      if (error.status === 409) {
        return 'Már létezik eszköz ezzel a vonalkóddal vagy azonosítóval';
      }
      return `Hiba (${error.status})`;
    }

    return 'Ismeretlen hiba';
  }

  private notifyError(message: string) {
    this.snackBar.open(message, 'Értem', {
      duration: 4000,
      horizontalPosition: 'right',
      verticalPosition: 'top',
      panelClass: ['error-snackbar'],
    });
  }
}
