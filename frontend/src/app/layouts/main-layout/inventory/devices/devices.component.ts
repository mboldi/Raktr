import {Component, OnInit} from '@angular/core';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatFormField, MatInput, MatLabel, MatSuffix} from "@angular/material/input";
import {DeviceService} from "../../../../services/device.service";
import {DeviceDetails} from "../../../../model/scannable/device/deviceDetails";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {DecimalPipe} from "@angular/common";
import {MatSortModule, Sort} from "@angular/material/sort";
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {MatFabButton, MatIconButton} from "@angular/material/button";
import {MatCard} from '@angular/material/card';
import {DeviceEditDialogComponent} from '../../../../components/device-edit-modal/device-edit-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {LocalStorageService} from '../../../../services/localStorage.service';
import {environment} from '../../../../../environments/environment';

const ALL_COLUMNS: string[] = ['name', 'assetTag', 'maker', 'model', 'quantity', 'category', 'location', 'weight'];
const REDUCED_COLUMNS: string[] = ['name', 'assetTag', 'maker', 'model', 'quantity'];

@Component({
  selector: 'app-devices',
  imports: [
    MatTable,
    MatFormField,
    MatLabel,
    MatInput,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatCellDef,
    MatHeaderCellDef,
    MatPaginator,
    MatHeaderRow,
    MatRow,
    MatHeaderRowDef,
    MatRowDef,
    DecimalPipe,
    MatSortModule,
    FormsModule,
    MatIcon,
    MatIconButton,
    MatSuffix,
    ReactiveFormsModule,
    MatCard,
    MatFabButton
  ],
  templateUrl: './devices.component.html',
  styleUrl: './devices.component.scss',
})
export class DevicesComponent implements OnInit {

  protected deviceSearchFormControl = new FormControl();

  protected displayedColumns = ALL_COLUMNS;

  protected devices: DeviceDetails[] = [];
  protected pagedDevices: DeviceDetails[] = [];

  private lastPageSetting: PageEvent | undefined;
  protected pageSize = 5;

  constructor(private deviceService: DeviceService,
              private dialog: MatDialog,
              private localStorageService: LocalStorageService,) {
  }

  ngOnInit() {
    const readPageSize = this.localStorageService.read(`${environment.defaultPageSizeKey}`);
    if(readPageSize) {
      this.pageSize = parseInt(readPageSize);
    }

    this.deviceService.getDevices().subscribe(devices => {
      this.devices = devices;
      this.pagedDevices = devices.slice(0, this.pageSize);
    });
  }

  protected applyFilter($event: KeyboardEvent) {
  }

  protected announceSortChange($event: Sort) {
    console.log($event);
  }

  protected openDevice(row: any) {
    const editDeviceDialog = this.dialog.open(DeviceEditDialogComponent, {
      width: '60vw',
      maxWidth: '100vw',
      data: row
    });

    editDeviceDialog.afterClosed().subscribe(result => {
      if(result) {
        // console.log(result);
        console.log("Yay, edited Device!")    // TODO: refresh device list
      }
    })
  }

  protected newDevice() {
    const editDeviceDialog = this.dialog.open(DeviceEditDialogComponent, {
      width: '60vw',
      maxWidth: '100vw'
    });

    editDeviceDialog.afterClosed().subscribe(result => {
      if(result) {
        console.log("Yay, created Device!")
        this.devices.push(result);
      }
    })
  }

  protected filterSortDevices() {

  }

  protected pageDevices(pageEvent : PageEvent) {
    this.lastPageSetting = pageEvent;
    this.localStorageService.write(`${environment.defaultPageSizeKey}`, pageEvent.pageSize.toString())

    const startId = (pageEvent.pageIndex)*pageEvent.pageSize;
    const endId = startId + pageEvent.pageSize;

    this.pagedDevices = this.devices.slice(startId, endId);
  }
}
