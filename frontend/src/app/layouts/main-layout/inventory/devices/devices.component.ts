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
import {DeviceDetails} from "../../../../model/scannable/device/DeviceDetails";
import {MatPaginator} from "@angular/material/paginator";
import {DecimalPipe} from "@angular/common";
import {MatSortModule, Sort} from "@angular/material/sort";
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {MatFabButton, MatIconButton} from "@angular/material/button";
import {MatCard} from '@angular/material/card';

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

  constructor(
    private deviceService: DeviceService,) {
  }

  ngOnInit() {
    this.deviceService.getDevices().subscribe(devices => {
      this.devices = devices;
    });
  }

  protected applyFilter($event: KeyboardEvent) {
  }

  protected announceSortChange($event: Sort) {
    console.log($event);
  }
}
