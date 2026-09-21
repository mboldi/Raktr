import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { MatCard, MatCardContent, MatCardHeader } from '@angular/material/card';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatProgressBar } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import type { CellValue } from 'exceljs';
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
          .catch(() => this.notifyError('Nem sikerült létrehozni az exportot!'))
          .finally(() => (this.exportingDevices = false));
      },
      error: () => {
        this.exportingDevices = false;
        this.notifyError('Nem sikerült létrehozni az exportot!');
      },
    });
  }

  private async downloadDevicesXlsx(devices: DeviceDetails[]) {
    const { default: ExcelJS } = await import('exceljs');
    const workbook = new ExcelJS.Workbook();
    const sheet = workbook.addWorksheet('Eszközök');

    sheet.columns = [
      { header: 'assetTag', key: 'assetTag', width: 16 },
      { header: 'barcode', key: 'barcode', width: 14 },
      { header: 'name', key: 'name', width: 28 },
      { header: 'manufacturer', key: 'manufacturer', width: 16 },
      { header: 'model', key: 'model', width: 16 },
      { header: 'serialNumber', key: 'serialNumber', width: 18 },
      { header: 'quantity', key: 'quantity', width: 10 },
      { header: 'category', key: 'category', width: 16 },
      { header: 'location', key: 'location', width: 16 },
      { header: 'owner', key: 'owner', width: 16 },
      { header: 'weight', key: 'weight', width: 10 },
      { header: 'estimatedValue', key: 'estimatedValue', width: 14 },
      { header: 'status', key: 'status', width: 16 },
      { header: 'publicRentable', key: 'publicRentable', width: 12 },
      { header: 'acquisitionSource', key: 'acquisitionSource', width: 18 },
      { header: 'acquisitionDate', key: 'acquisitionDate', width: 16 },
      { header: 'warrantyEndDate', key: 'warrantyEndDate', width: 16 },
      { header: 'notes', key: 'notes', width: 30 },
      { header: 'deleted', key: 'deleted', width: 10 },
      { header: 'createdAt', key: 'createdAt', width: 18 },
      { header: 'createdBy', key: 'createdBy', width: 16 },
      { header: 'updatedAt', key: 'updatedAt', width: 18 },
      { header: 'updatedBy', key: 'updatedBy', width: 16 },
    ];
    sheet.getRow(1).font = { bold: true };

    devices.forEach((device) =>
      sheet.addRow({
        assetTag: device.assetTag,
        barcode: device.barcode,
        name: device.name,
        manufacturer: device.manufacturer,
        model: device.model,
        serialNumber: device.serialNumber,
        quantity: device.quantity,
        category: device.category,
        location: device.location,
        owner: device.owner?.name,
        weight: device.weight,
        estimatedValue: device.estimatedValue,
        status: device.status,
        publicRentable: device.publicRentable,
        acquisitionSource: device.acquisitionSource,
        acquisitionDate: device.acquisitionDate,
        warrantyEndDate: device.warrantyEndDate,
        notes: device.notes,
        deleted: device.deleted,
        createdAt: device.createdAt,
        createdBy: device.createdBy?.nickname,
        updatedAt: device.updatedAt,
        updatedBy: device.updatedBy?.nickname,
      }),
    );

    sheet.getColumn('acquisitionDate').numFmt = 'yyyy. mm. dd.';
    sheet.getColumn('warrantyEndDate').numFmt = 'yyyy. mm. dd.';
    sheet.getColumn('createdAt').numFmt = 'yyyy. mm. dd. hh:mm';
    sheet.getColumn('updatedAt').numFmt = 'yyyy. mm. dd. hh:mm';

    const buffer = await workbook.xlsx.writeBuffer();
    const blob = new Blob([buffer], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    });

    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `eszkozok_${new Date().toISOString().split('T')[0]}.xlsx`;
    link.click();
    URL.revokeObjectURL(url);
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
    const buffer = await file.arrayBuffer();
    const { default: ExcelJS } = await import('exceljs');
    const workbook = new ExcelJS.Workbook();
    await workbook.xlsx.load(buffer);

    const sheet = workbook.worksheets[0];
    if (!sheet) {
      return [];
    }

    const headers: string[] = [];
    sheet.getRow(1).eachCell({ includeEmpty: true }, (cell, colNumber) => {
      headers[colNumber] = this.cellToString(cell.value);
    });

    const rows: Record<string, CellValue>[] = [];
    sheet.eachRow((row, rowNumber) => {
      if (rowNumber === 1) {
        return;
      }

      const record: Record<string, CellValue> = {};
      row.eachCell({ includeEmpty: true }, (cell, colNumber) => {
        const header = headers[colNumber];
        if (header) {
          record[header] = cell.value;
        }
      });
      rows.push(record);
    });

    return rows;
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

  private cellToString(value: CellValue | undefined): string {
    if (value === null || value === undefined) {
      return '';
    }
    if (typeof value === 'object' && 'text' in value) {
      return String((value as { text?: unknown }).text ?? '').trim();
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
