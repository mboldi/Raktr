import {Component} from '@angular/core';
import {MatCard, MatCardContent, MatCardHeader} from '@angular/material/card';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Workbook} from 'exceljs';
import {DeviceService} from '../../../../services/device.service';
import {DeviceDetails} from '../../../../model/scannable/device/deviceDetails';

@Component({
  selector: 'app-export-import',
  imports: [
    MatCard,
    MatCardHeader,
    MatCardContent,
    MatButton,
    MatIcon,
    MatProgressSpinner,
  ],
  templateUrl: './export-import.component.html',
  styleUrl: './export-import.component.scss',
})
export class ExportImportComponent {
  protected exportingDevices = false;

  constructor(
    private deviceService: DeviceService,
    private snackBar: MatSnackBar,
  ) {
  }

  protected exportDevices() {
    this.exportingDevices = true;

    this.deviceService.getDevices().subscribe({
      next: devices => {
        this.downloadDevicesXlsx(devices)
          .catch(() => this.notifyError())
          .finally(() => this.exportingDevices = false);
      },
      error: () => {
        this.exportingDevices = false;
        this.notifyError();
      }
    });
  }

  private async downloadDevicesXlsx(devices: DeviceDetails[]) {
    const workbook = new Workbook();
    const sheet = workbook.addWorksheet('Eszközök');

    sheet.columns = [
      {header: 'assetTag', key: 'assetTag', width: 16},
      {header: 'barcode', key: 'barcode', width: 14},
      {header: 'name', key: 'name', width: 28},
      {header: 'manufacturer', key: 'manufacturer', width: 16},
      {header: 'model', key: 'model', width: 16},
      {header: 'serialNumber', key: 'serialNumber', width: 18},
      {header: 'quantity', key: 'quantity', width: 10},
      {header: 'category', key: 'category', width: 16},
      {header: 'location', key: 'location', width: 16},
      {header: 'owner', key: 'owner', width: 16},
      {header: 'weight', key: 'weight', width: 10},
      {header: 'estimatedValue', key: 'estimatedValue', width: 14},
      {header: 'status', key: 'status', width: 16},
      {header: 'publicRentable', key: 'publicRentable', width: 12},
      {header: 'acquisitionSource', key: 'acquisitionSource', width: 18},
      {header: 'acquisitionDate', key: 'acquisitionDate', width: 16},
      {header: 'warrantyEndDate', key: 'warrantyEndDate', width: 16},
      {header: 'notes', key: 'notes', width: 30},
      {header: 'deleted', key: 'deleted', width: 10},
      {header: 'createdAt', key: 'createdAt', width: 18},
      {header: 'createdBy', key: 'createdBy', width: 16},
      {header: 'updatedAt', key: 'updatedAt', width: 18},
      {header: 'updatedBy', key: 'updatedBy', width: 16},
    ];
    sheet.getRow(1).font = {bold: true};

    devices.forEach(device => sheet.addRow({
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
    }));

    sheet.getColumn('acquisitionDate').numFmt = 'yyyy. mm. dd.';
    sheet.getColumn('warrantyEndDate').numFmt = 'yyyy. mm. dd.';
    sheet.getColumn('createdAt').numFmt = 'yyyy. mm. dd. hh:mm';
    sheet.getColumn('updatedAt').numFmt = 'yyyy. mm. dd. hh:mm';

    const buffer = await workbook.xlsx.writeBuffer();
    const blob = new Blob([buffer], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    });

    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `eszkozok_${new Date().toISOString().split('T')[0]}.xlsx`;
    link.click();
    URL.revokeObjectURL(url);
  }

  private notifyError() {
    this.snackBar.open('Nem sikerült létrehozni az exportot!', 'Értem', {
      duration: 4000,
      horizontalPosition: 'right',
      verticalPosition: 'top',
      panelClass: ['error-snackbar'],
    });
  }
}
