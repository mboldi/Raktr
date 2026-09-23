import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { MatCard, MatCardContent, MatCardHeader } from '@angular/material/card';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatProgressBar } from '@angular/material/progress-bar';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom, Observable } from 'rxjs';
import type { Column, Value } from 'write-excel-file/browser';
import { DeviceService } from '../../../../services/device.service';
import { ContainerService } from '../../../../services/container.service';
import { CategoryService } from '../../../../services/category.service';
import { LocationService } from '../../../../services/location.service';
import { OwnerService } from '../../../../services/owner.service';
import { ScannableService } from '../../../../services/scannable.service';
import { DeviceDetails } from '../../../../model/scannable/device/deviceDetails';
import { ContainerDetails } from '../../../../model/scannable/container/containerDetails';
import { ContainerCreateDto } from '../../../../model/scannable/container/containerCreateDto';
import { ContainerUpdateDto } from '../../../../model/scannable/container/containerUpdateDto';
import { DeviceCreateDto } from '../../../../model/scannable/device/deviceCreateDto';
import { DeviceUpdateDto } from '../../../../model/scannable/device/deviceUpdateDto';
import { DeviceStatus } from '../../../../model/scannable/device/deviceStatus';
import { OwnerCreateDto } from '../../../../model/owner/ownerCreateDto';
import { environment } from '../../../../../environments/environment';
import { Title } from '@angular/platform-browser';

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

// `DeviceCreateDto` and `DeviceUpdateDto` take identical constructor parameters in the same
// order, so a single field tuple built once per row can construct whichever one is needed.
type DeviceDtoFields = [
  assetTag: string,
  barcode: string,
  name: string,
  weight: number,
  publicRentable: boolean,
  categoryName: string,
  locationName: string,
  ownerId: number,
  manufacturer: string,
  model: string,
  serialNumber: string,
  estimatedValue: number,
  status: DeviceStatus,
  quantity: number,
  acquisitionSource: string,
  acquisitionDate: Date | null,
  warrantyEndDate: Date | null,
  notes: string,
];

// `ContainerCreateDto` and `ContainerUpdateDto` likewise share one constructor shape.
type ContainerDtoFields = [
  assetTag: string,
  barcode: string,
  name: string,
  weight: number,
  publicRentable: boolean,
  categoryName: string,
  locationName: string,
  ownerId: number,
];

function excelColumn<T>(
  header: string,
  width: number,
  value: (item: T) => Value | null | undefined,
  format?: string,
): Column<T> {
  return {
    header: { value: header, fontWeight: 'bold' },
    width,
    cell: (item) => {
      const cellValue = value(item);
      return cellValue === null || cellValue === undefined ? null : { value: cellValue, format };
    },
  };
}

const DEVICE_EXPORT_COLUMNS: Column<DeviceDetails>[] = [
  excelColumn('assetTag', 16, (device: DeviceDetails) => device.assetTag),
  excelColumn('barcode', 14, (device: DeviceDetails) => device.barcode),
  excelColumn('name', 28, (device: DeviceDetails) => device.name),
  excelColumn('manufacturer', 16, (device: DeviceDetails) => device.manufacturer),
  excelColumn('model', 16, (device: DeviceDetails) => device.model),
  excelColumn('serialNumber', 18, (device: DeviceDetails) => device.serialNumber),
  excelColumn('quantity', 10, (device: DeviceDetails) => device.quantity),
  excelColumn('category', 16, (device: DeviceDetails) => device.category),
  excelColumn('location', 16, (device: DeviceDetails) => device.location),
  excelColumn('owner', 16, (device: DeviceDetails) => device.owner?.name),
  excelColumn('weight', 10, (device: DeviceDetails) => device.weight),
  excelColumn('estimatedValue', 14, (device: DeviceDetails) => device.estimatedValue),
  excelColumn('status', 16, (device: DeviceDetails) => device.status),
  excelColumn('publicRentable', 12, (device: DeviceDetails) => device.publicRentable),
  excelColumn('acquisitionSource', 18, (device: DeviceDetails) => device.acquisitionSource),
  excelColumn(
    'acquisitionDate',
    16,
    (device: DeviceDetails) => device.acquisitionDate,
    'yyyy. mm. dd.',
  ),
  excelColumn(
    'warrantyEndDate',
    16,
    (device: DeviceDetails) => device.warrantyEndDate,
    'yyyy. mm. dd.',
  ),
  excelColumn('notes', 30, (device: DeviceDetails) => device.notes),
  excelColumn('createdAt', 18, (device: DeviceDetails) => device.createdAt, 'yyyy. mm. dd. hh:mm'),
  excelColumn('createdBy', 16, (device: DeviceDetails) => device.createdBy?.nickname),
  excelColumn('updatedAt', 18, (device: DeviceDetails) => device.updatedAt, 'yyyy. mm. dd. hh:mm'),
  excelColumn('updatedBy', 16, (device: DeviceDetails) => device.updatedBy?.nickname),
];

const CONTAINER_EXPORT_COLUMNS: Column<ContainerDetails>[] = [
  excelColumn('assetTag', 16, (container: ContainerDetails) => container.assetTag),
  excelColumn('barcode', 14, (container: ContainerDetails) => container.barcode),
  excelColumn('name', 28, (container: ContainerDetails) => container.name),
  excelColumn('category', 16, (container: ContainerDetails) => container.category),
  excelColumn('location', 16, (container: ContainerDetails) => container.location),
  excelColumn('owner', 16, (container: ContainerDetails) => container.owner?.name),
  excelColumn('weight', 10, (container: ContainerDetails) => container.weight),
  excelColumn('totalWeight', 12, (container: ContainerDetails) => container.totalWeight),
  excelColumn('publicRentable', 12, (container: ContainerDetails) => container.publicRentable),
  excelColumn(
    'createdAt',
    18,
    (container: ContainerDetails) => container.createdAt,
    'yyyy. mm. dd. hh:mm',
  ),
  excelColumn('createdBy', 16, (container: ContainerDetails) => container.createdBy?.nickname),
  excelColumn(
    'updatedAt',
    18,
    (container: ContainerDetails) => container.updatedAt,
    'yyyy. mm. dd. hh:mm',
  ),
  excelColumn('updatedBy', 16, (container: ContainerDetails) => container.updatedBy?.nickname),
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
  private containerService = inject(ContainerService);
  private categoryService = inject(CategoryService);
  private locationService = inject(LocationService);
  private ownerService = inject(OwnerService);
  private scannableService = inject(ScannableService);
  private snackBar = inject(MatSnackBar);
  private titleService = inject(Title);

  constructor() {
    this.titleService.setTitle('Raktr - Export/Import');
  }

  protected exportingDevices = false;
  protected exportingContainers = false;

  protected importing = false;
  protected importKind: 'device' | 'container' | null = null;
  protected importProgress: ImportProgress | null = null;
  protected importFailures: ImportFailure[] = [];

  private nextBarcodeCounter = 0;

  protected exportDevices() {
    this.exportingDevices = true;

    this.deviceService.getDevices().subscribe({
      next: (devices) => {
        this.downloadXlsx(devices, DEVICE_EXPORT_COLUMNS, 'Eszközök', 'eszkozok')
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

  protected exportContainers() {
    this.exportingContainers = true;

    this.containerService.getContainers().subscribe({
      next: (containers) => {
        this.downloadXlsx(containers, CONTAINER_EXPORT_COLUMNS, 'Szállítóládák', 'szallitoladak')
          .catch((error: unknown) => {
            console.error('Container export failed:', error);
            this.notifyError('Nem sikerült létrehozni az exportot!');
          })
          .finally(() => (this.exportingContainers = false));
      },
      error: (error: unknown) => {
        console.error('Failed to load containers for export:', error);
        this.exportingContainers = false;
        this.notifyError('Nem sikerült létrehozni az exportot!');
      },
    });
  }

  private async downloadXlsx<T extends object>(
    items: T[],
    columns: Column<T>[],
    sheetName: string,
    fileNamePrefix: string,
  ) {
    const { default: writeExcelFile } = await import('write-excel-file/browser');

    // write-excel-file only accepts plain objects for its `Object[] + columns` API (it checks
    // `value.constructor === Object`) - our model classes fail that check and get rejected as
    // an invalid first argument, so they're copied into plain objects here first.
    await writeExcelFile(
      items.map((item) => ({ ...item })),
      {
        sheet: sheetName,
        columns,
      },
    ).toFile(`${fileNamePrefix}_${new Date().toISOString().split('T')[0]}.xlsx`);
  }

  protected onDeviceFileSelected(event: Event) {
    const file = this.takeSelectedFile(event);
    if (file) {
      this.importDevices(file);
    }
  }

  protected onContainerFileSelected(event: Event) {
    const file = this.takeSelectedFile(event);
    if (file) {
      this.importContainers(file);
    }
  }

  private takeSelectedFile(event: Event): File | undefined {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = '';
    return file;
  }

  private async importDevices(file: File) {
    const existingDevices = await firstValueFrom(this.deviceService.getDevices());
    const deviceByBarcode = new Map(existingDevices.map((device) => [device.barcode, device]));
    const deviceByAssetTag = new Map(existingDevices.map((device) => [device.assetTag, device]));

    await this.runImport(file, 'device', (row, ownerIdByName) =>
      this.buildDeviceFields(row, ownerIdByName, deviceByBarcode, deviceByAssetTag).then(
        ({ existing, fields }) => ({
          existing,
          create: () => this.deviceService.createDevice(new DeviceCreateDto(...fields)),
          update: () =>
            this.deviceService.updateDevice(existing!.id, new DeviceUpdateDto(...fields)),
        }),
      ),
    );
  }

  private async importContainers(file: File) {
    const existingContainers = await firstValueFrom(this.containerService.getContainers());
    const containerByBarcode = new Map(
      existingContainers.map((container) => [container.barcode, container]),
    );
    const containerByAssetTag = new Map(
      existingContainers.map((container) => [container.assetTag, container]),
    );

    await this.runImport(file, 'container', (row, ownerIdByName) =>
      this.buildContainerFields(row, ownerIdByName, containerByBarcode, containerByAssetTag).then(
        ({ existing, fields }) => ({
          existing,
          create: () => this.containerService.createContainer(new ContainerCreateDto(...fields)),
          update: () =>
            this.containerService.updateContainer(existing!.id, new ContainerUpdateDto(...fields)),
        }),
      ),
    );
  }

  /** Shared row-by-row import driver: parses the file, makes sure every referenced
   * category/location/owner exists, then lets `buildRow` decide - per row - whether an existing
   * item should be updated or a new one created, tracking progress/failures/counts throughout. */
  private async runImport<TExisting extends { id: number }>(
    file: File,
    kind: 'device' | 'container',
    buildRow: (
      row: Record<string, CellValue>,
      ownerIdByName: Map<string, number>,
    ) => Promise<{
      existing: TExisting | undefined;
      create: () => Observable<unknown>;
      update: () => Observable<unknown>;
    }>,
  ) {
    this.importing = true;
    this.importKind = kind;
    this.importFailures = [];
    this.importProgress = null;

    let rows: Record<string, CellValue>[];
    try {
      rows = await this.parseXlsxFile(file);
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

    let createdCount = 0;
    let updatedCount = 0;

    for (let i = 0; i < rows.length; i++) {
      try {
        const { existing, create, update } = await buildRow(rows[i], ownerIdByName);

        if (existing) {
          await firstValueFrom(update());
          updatedCount++;
        } else {
          await firstValueFrom(create());
          createdCount++;
        }
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

    const parts = [];
    if (createdCount > 0) {
      parts.push(`${createdCount} létrehozva`);
    }
    if (updatedCount > 0) {
      parts.push(`${updatedCount} frissítve`);
    }
    if (this.importFailures.length > 0) {
      parts.push(`${this.importFailures.length} sikertelen`);
    }

    this.snackBar.open(`Import kész: ${parts.join(', ')}.`, 'Rendben', {
      duration: 4000,
      horizontalPosition: 'right',
      verticalPosition: 'top',
      panelClass: [this.importFailures.length === 0 ? 'success-snackbar' : 'error-snackbar'],
    });
  }

  private async parseXlsxFile(file: File): Promise<Record<string, CellValue>[]> {
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

  /** A row matching an existing item (by either identifier) updates that item in place instead
   * of creating a duplicate - its own asset tag/barcode are kept as-is, so a blank cell on the
   * row doesn't trigger generating a new code for an already-identified item. Only genuinely
   * new items (no match, and missing one or both identifiers) get a generated code. */
  private async resolveIdentity<T extends { assetTag: string; barcode: string }>(
    row: Record<string, CellValue>,
    byBarcode: Map<string, T>,
    byAssetTag: Map<string, T>,
  ): Promise<{ assetTag: string; barcode: string; existing: T | undefined }> {
    let assetTag = this.cellToString(row['assetTag']);
    let barcode = this.cellToString(row['barcode']);

    if (!assetTag && barcode) {
      assetTag = barcode;
    }

    const existing =
      (barcode ? byBarcode.get(barcode) : undefined) ??
      (assetTag ? byAssetTag.get(assetTag) : undefined);

    if (existing) {
      assetTag = existing.assetTag;
      barcode = existing.barcode;
    } else if (!assetTag || !barcode) {
      const generated = await this.generateNextFreeBarcode();
      if (!barcode) {
        barcode = generated;
      }
      if (!assetTag) {
        assetTag = generated;
      }
    }

    return { assetTag, barcode, existing };
  }

  private async buildDeviceFields(
    row: Record<string, CellValue>,
    ownerIdByName: Map<string, number>,
    deviceByBarcode: Map<string, DeviceDetails>,
    deviceByAssetTag: Map<string, DeviceDetails>,
  ): Promise<{ existing: DeviceDetails | undefined; fields: DeviceDtoFields }> {
    const { assetTag, barcode, existing } = await this.resolveIdentity(
      row,
      deviceByBarcode,
      deviceByAssetTag,
    );

    const ownerName = this.cellToString(row['owner']) || environment.defaultOwnerName;
    const status = this.cellToString(row['status']) || environment.defaultDeviceStatus;
    const acquisitionDate = this.cellToDate(row['acquisitionDate']) ?? new Date();

    return {
      existing,
      fields: [
        assetTag,
        barcode,
        this.cellToString(row['name']),
        this.cellToNumber(row['weight']) ?? 1000,
        this.cellToBoolean(row['publicRentable']),
        this.cellToString(row['category']),
        this.cellToString(row['location']),
        ownerIdByName.get(ownerName) as number,
        this.cellToString(row['manufacturer']),
        this.cellToString(row['model']),
        this.cellToString(row['serialNumber']),
        this.cellToNumber(row['estimatedValue']) ?? 1,
        status as DeviceStatus,
        this.cellToNumber(row['quantity']) ?? environment.defaultDeviceQuantity,
        this.cellToString(row['acquisitionSource']),
        acquisitionDate,
        this.cellToDate(row['warrantyEndDate']) ?? null,
        this.cellToString(row['notes']),
      ],
    };
  }

  private async buildContainerFields(
    row: Record<string, CellValue>,
    ownerIdByName: Map<string, number>,
    containerByBarcode: Map<string, ContainerDetails>,
    containerByAssetTag: Map<string, ContainerDetails>,
  ): Promise<{ existing: ContainerDetails | undefined; fields: ContainerDtoFields }> {
    const { assetTag, barcode, existing } = await this.resolveIdentity(
      row,
      containerByBarcode,
      containerByAssetTag,
    );

    const ownerName = this.cellToString(row['owner']) || environment.defaultOwnerName;

    return {
      existing,
      fields: [
        assetTag,
        barcode,
        this.cellToString(row['name']),
        this.cellToNumber(row['weight']) ?? 1000,
        this.cellToBoolean(row['publicRentable']),
        this.cellToString(row['category']),
        this.cellToString(row['location']),
        ownerIdByName.get(ownerName) as number,
      ],
    };
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
        return 'Már létezik tétel ezzel a vonalkóddal vagy azonosítóval';
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
