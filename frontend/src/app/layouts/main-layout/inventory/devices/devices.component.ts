import {Component, effect, OnInit, ViewChild} from '@angular/core';
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
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatSnackBar} from "@angular/material/snack-bar";
import {TabbedEditModalComponent} from '../../../../components/tabbed-edit-modal/tabbed-edit-modal.component';
import {WindowWidthService} from '../../../../services/windowWidth.service';

const ALL_COLUMNS: string[] = ['name', 'assetTag', 'maker', 'model', 'quantity', 'category', 'location', 'weight'];
const REDUCED_COLUMNS: string[] = ['name', 'assetTag', 'maker', 'model'];

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
    MatFabButton,
    MatProgressSpinner
  ],
  templateUrl: './devices.component.html',
  styleUrl: './devices.component.scss',
})
export class DevicesComponent implements OnInit {

  protected loading: boolean = true;
  @ViewChild(MatTable) table!: MatTable<DeviceDetails>;

  protected deviceSearchFormControl = new FormControl();
  private searchFilter = "";

  protected displayedColumns = ALL_COLUMNS;

  protected devices: DeviceDetails[] = [];
  protected filteredDevices: DeviceDetails[] = [];
  protected pagedDevices: DeviceDetails[] = [];

  private lastPageSetting: PageEvent | undefined;
  protected pageSize = 5;

  private lastSort: Sort = {active: 'name', direction: 'asc'};

  constructor(
    private windowService: WindowWidthService,
    private deviceService: DeviceService,
    private dialog: MatDialog,
    private localStorageService: LocalStorageService,
    private snackBar: MatSnackBar,) {

    effect(() => {
      const width = this.windowService.windowWidth();
      this.displayedColumns = width >= 1200 ? ALL_COLUMNS : REDUCED_COLUMNS;
    });
  }

  ngOnInit() {
    const readPageSize = this.localStorageService.read(`${environment.defaultPageSizeKey}`);
    if (readPageSize) {
      this.pageSize = parseInt(readPageSize);
    }

    this.deviceService.getDevices().subscribe(devices => {
      this.devices = devices;
      this.filteredDevices = devices;
      this.pagedDevices = devices.slice(0, this.pageSize);

      this.loading = false;
    });
  }

  protected openDevice(row: any) {
    const viewDeviceDialog = this.dialog.open(TabbedEditModalComponent, {
      width: '60vw',
      maxWidth: '100vw',
      position: {top: '40px'},
      data: row
    });

    viewDeviceDialog.afterClosed().subscribe(result => {
      if (result === 'edit') {
        const editDeviceDialog = this.dialog.open(DeviceEditDialogComponent, {
          width: '60vw',
          maxWidth: '100vw',
          position: {top: '40px'},
          data: row
        });

        editDeviceDialog.afterClosed().subscribe(result => {
          if (result) {
            this.replaceById(this.pagedDevices, result);
            this.table.renderRows();

            this.snackBar.open(`${result.name} frissítve!`, "Remek!", {
              duration: 3000,
              horizontalPosition: 'right',
              verticalPosition: 'top',
              panelClass: ['success-snackbar'],
            });
          }
        });
      }
    });
  }

  protected newDevice() {
    const editDeviceDialog = this.dialog.open(DeviceEditDialogComponent, {
      width: '60vw',
      maxWidth: '100vw'
    });

    editDeviceDialog.afterClosed().subscribe(result => {
      if (result) {
        this.devices.push(result);
        this.filterSortDevices();

        this.snackBar.open(`${result.name} létrehozva!`, "Kitűnő!", {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['success-snackbar'],
        });
      }
    })
  }

  protected applyFilter($event: KeyboardEvent) {
    this.searchFilter = this.deviceSearchFormControl.value;

    this.filterSortDevices();
  }

  protected announceSortChange($event: Sort) {
    console.log($event);
  }

  protected filterSortDevices() {
    this.filteredDevices = this.devices.filter(device =>
      device.name.toLowerCase().includes(this.searchFilter.toLowerCase()) ||
      device.assetTag.toLowerCase().includes(this.searchFilter.toLowerCase()) ||
      device.model.toLowerCase().includes(this.searchFilter.toLowerCase())
    )

    if (this.lastPageSetting !== undefined) {
      this.pageDevices(this.lastPageSetting);
    } else {
      this.pagedDevices = this.filteredDevices.slice(0, this.pageSize);
    }
  }

  protected pageDevices(pageEvent: PageEvent) {
    this.lastPageSetting = pageEvent;
    this.pageSize = pageEvent.pageSize;
    this.localStorageService.write(`${environment.defaultPageSizeKey}`, pageEvent.pageSize.toString())

    const startId = (pageEvent.pageIndex) * pageEvent.pageSize;
    const endId = startId + pageEvent.pageSize;

    this.pagedDevices = this.filteredDevices.slice(startId, endId);
  }

  protected replaceById<T extends { id: string | number }>(array: T[], newObject: T): void {
    const index = array.findIndex(item => item.id === newObject.id);
    if (index !== -1) {
      array[index] = newObject;
    }
  }

  protected resetFilter() {
    this.deviceSearchFormControl.reset();
    this.searchFilter = "";
    this.filterSortDevices();
  }
}
