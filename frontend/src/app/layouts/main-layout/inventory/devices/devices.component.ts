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
import {MatButton, MatFabButton, MatIconButton} from "@angular/material/button";
import {MatCard} from '@angular/material/card';
import {DeviceEditDialogComponent} from '../../../../components/device-edit-modal/device-edit-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {LocalStorageService} from '../../../../services/localStorage.service';
import {environment} from '../../../../../environments/environment';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatSnackBar} from "@angular/material/snack-bar";
import {
  TabbedEditModalComponent,
  TabbedEditModalData
} from '../../../../components/tabbed-edit-modal/tabbed-edit-modal.component';
import {WindowWidthService} from '../../../../services/windowWidth.service';
import {CategoryService} from '../../../../services/category.service';
import {LocationService} from '../../../../services/location.service';
import {OwnerService} from '../../../../services/owner.service';
import {CategoryDetails} from '../../../../model/category/categoryDetails';
import {LocationDetails} from '../../../../model/location/LocationDetails';
import {OwnerDetailsDto} from '../../../../model/owner/ownerDetailsDto';
import {MatCheckbox} from '@angular/material/checkbox';

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
    MatProgressSpinner,
    MatCheckbox,
    MatButton
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

  protected filterPanelOpen = false;

  protected categories: CategoryDetails[] = [];
  protected locations: LocationDetails[] = [];
  protected owners: OwnerDetailsDto[] = [];
  protected makers: string[] = [];

  protected visibleCategories: CategoryDetails[] = [];
  protected visibleLocations: LocationDetails[] = [];
  protected visibleOwners: OwnerDetailsDto[] = [];
  protected visibleMakers: string[] = [];

  protected selectedCategories = new Set<string>();
  protected selectedLocations = new Set<string>();
  protected selectedOwners = new Set<string>();
  protected selectedMakers = new Set<string>();

  constructor(
    private windowService: WindowWidthService,
    private dialog: MatDialog,
    private localStorageService: LocalStorageService,
    private snackBar: MatSnackBar,
    private deviceService: DeviceService,
    private categoryService: CategoryService,
    private locationService: LocationService,
    private ownerService: OwnerService,
    ) {

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
      this.updateMakers();
      this.filterSortDevices();

      this.loading = false;
    });

    this.categoryService.getCategories().subscribe(categories => {
      this.categories = categories;
      this.updateVisibleFilterOptions();
    });
    this.locationService.getLocations().subscribe(locations => {
      this.locations = locations;
      this.updateVisibleFilterOptions();
    });
    this.ownerService.getOwners().subscribe(owners => {
      this.owners = owners;
      this.updateVisibleFilterOptions();
    });
  }

  private updateMakers() {
    this.makers = Array.from(new Set(
      this.devices
        .map(device => device.manufacturer)
        .filter(manufacturer => !!manufacturer)
    )).sort((a, b) => a.localeCompare(b));
  }

  protected openDevice(row: any) {
    const viewDeviceDialog = this.dialog.open(TabbedEditModalComponent, {
      width: '60vw',
      maxWidth: '100vw',
      position: {top: '40px'},
      data: {kind: 'device', item: row} as TabbedEditModalData
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
        this.updateMakers();
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
    this.lastSort = $event;
    this.filterSortDevices();
  }

  protected filterSortDevices() {
    this.filteredDevices = this.devices.filter(device =>
      this.matchesSearch(device) &&
      (this.selectedCategories.size === 0 || this.selectedCategories.has(device.category)) &&
      (this.selectedLocations.size === 0 || this.selectedLocations.has(device.location)) &&
      (this.selectedOwners.size === 0 || this.selectedOwners.has(device.owner?.name ?? '')) &&
      (this.selectedMakers.size === 0 || this.selectedMakers.has(device.manufacturer))
    )

    this.sortDevices();
    this.updateVisibleFilterOptions();

    if (this.lastPageSetting !== undefined) {
      this.pageDevices(this.lastPageSetting);
    } else {
      this.pagedDevices = this.filteredDevices.slice(0, this.pageSize);
    }
  }

  private sortDevices() {
    const {active, direction} = this.lastSort;
    const comparator = direction ? this.getSortComparator(active) : null;
    if (!comparator) {
      return;
    }

    this.filteredDevices = this.filteredDevices.slice().sort((a, b) =>
      direction === 'asc' ? comparator(a, b) : -comparator(a, b));
  }

  private getSortComparator(active: string): ((a: DeviceDetails, b: DeviceDetails) => number) | null {
    switch (active) {
      case 'name':
        return (a, b) => this.compareStrings(a.name, b.name);
      case 'assetTag':
        return (a, b) => this.compareStrings(a.assetTag, b.assetTag);
      case 'maker':
        return (a, b) => this.compareStrings(a.manufacturer, b.manufacturer);
      case 'model':
        return (a, b) => this.compareStrings(a.model, b.model);
      case 'weight':
        return (a, b) => a.weight - b.weight;
      default:
        return null;
    }
  }

  private compareStrings(a: string | null | undefined, b: string | null | undefined): number {
    return (a ?? '').localeCompare(b ?? '');
  }

  private matchesSearch(device: DeviceDetails): boolean {
    const search = this.searchFilter.toLowerCase();
    return (device.name ?? '').toLowerCase().includes(search) ||
      (device.assetTag ?? '').toLowerCase().includes(search) ||
      (device.model ?? '').toLowerCase().includes(search);
  }

  private devicesMatchingExcept(excludedFacet: 'category' | 'location' | 'owner' | 'maker'): DeviceDetails[] {
    return this.devices.filter(device =>
      this.matchesSearch(device) &&
      (excludedFacet === 'category' || this.selectedCategories.size === 0 || this.selectedCategories.has(device.category)) &&
      (excludedFacet === 'location' || this.selectedLocations.size === 0 || this.selectedLocations.has(device.location)) &&
      (excludedFacet === 'owner' || this.selectedOwners.size === 0 || this.selectedOwners.has(device.owner?.name ?? '')) &&
      (excludedFacet === 'maker' || this.selectedMakers.size === 0 || this.selectedMakers.has(device.manufacturer))
    );
  }

  private updateVisibleFilterOptions() {
    const availableCategories = new Set(this.devicesMatchingExcept('category').map(device => device.category));
    this.visibleCategories = this.categories.filter(category =>
      availableCategories.has(category.name) || this.selectedCategories.has(category.name));

    const availableLocations = new Set(this.devicesMatchingExcept('location').map(device => device.location));
    this.visibleLocations = this.locations.filter(location =>
      availableLocations.has(location.name) || this.selectedLocations.has(location.name));

    const availableOwners = new Set(this.devicesMatchingExcept('owner').map(device => device.owner?.name ?? ''));
    this.visibleOwners = this.owners.filter(owner =>
      availableOwners.has(owner.name) || this.selectedOwners.has(owner.name));

    const availableMakers = new Set(this.devicesMatchingExcept('maker').map(device => device.manufacturer));
    this.visibleMakers = this.makers.filter(maker =>
      availableMakers.has(maker) || this.selectedMakers.has(maker));
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

  protected toggleFilterPanel() {
    this.filterPanelOpen = !this.filterPanelOpen;
  }

  protected closeFilterPanel() {
    this.filterPanelOpen = false;
  }

  protected toggleFilterValue(selectedValues: Set<string>, value: string) {
    if (selectedValues.has(value)) {
      selectedValues.delete(value);
    } else {
      selectedValues.add(value);
    }

    this.filterSortDevices();
  }

  protected hasActiveFilters(): boolean {
    return this.selectedCategories.size > 0 ||
      this.selectedLocations.size > 0 ||
      this.selectedOwners.size > 0 ||
      this.selectedMakers.size > 0;
  }

  protected clearFilters() {
    this.selectedCategories.clear();
    this.selectedLocations.clear();
    this.selectedOwners.clear();
    this.selectedMakers.clear();

    this.filterSortDevices();
  }
}
