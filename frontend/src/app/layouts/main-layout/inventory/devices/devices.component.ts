import {
  Component,
  effect,
  ElementRef,
  OnInit,
  ViewChild,
  ChangeDetectionStrategy,
  inject,
} from '@angular/core';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
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
  MatTable,
} from '@angular/material/table';
import { MatFormField, MatInput, MatLabel, MatSuffix } from '@angular/material/input';
import { DeviceService } from '../../../../services/device.service';
import { DeviceDetails } from '../../../../model/scannable/device/deviceDetails';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { DecimalPipe } from '@angular/common';
import { MatSortModule, Sort } from '@angular/material/sort';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { MatButton, MatFabButton, MatIconButton } from '@angular/material/button';
import { MatCard } from '@angular/material/card';
import {
  DeviceDialogData,
  DeviceEditDialogComponent,
} from '../../../../components/device-edit-modal/device-edit-dialog.component';
import { YesnoModalComponent } from '../../../../components/yesno-modal/yesno-modal.component';
import { MatDialog } from '@angular/material/dialog';
import { LocalStorageService } from '../../../../services/localStorage.service';
import { environment } from '../../../../../environments/environment';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import {
  TabbedEditModalComponent,
  TabbedEditModalData,
} from '../../../../components/tabbed-edit-modal/tabbed-edit-modal.component';
import { WindowWidthService } from '../../../../services/windowWidth.service';
import { CategoryService } from '../../../../services/category.service';
import { LocationService } from '../../../../services/location.service';
import { OwnerService } from '../../../../services/owner.service';
import { AdminAccessService } from '../../../../services/adminAccess.service';
import { CategoryDetails } from '../../../../model/category/categoryDetails';
import { LocationDetails } from '../../../../model/location/LocationDetails';
import { OwnerDetailsDto } from '../../../../model/owner/ownerDetailsDto';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatChip, MatChipRemove, MatChipSet } from '@angular/material/chips';
import { MatTooltip } from '@angular/material/tooltip';
import { Location, NgTemplateOutlet } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Title } from '@angular/platform-browser';

const ALL_COLUMNS: string[] = [
  'name',
  'assetTag',
  'maker',
  'model',
  'quantity',
  'category',
  'location',
  'owner',
  'weight',
];
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
    MatButton,
    MatChip,
    MatChipRemove,
    MatChipSet,
    MatTooltip,
    NgTemplateOutlet,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger,
  ],
  templateUrl: './devices.component.html',
  host: {
    '(document:keydown.escape)': 'closeFilterPanel()',
    '(document:contextmenu)': 'onDocumentContextMenu($event)',
  },
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './devices.component.scss',
})
export class DevicesComponent implements OnInit {
  private windowService = inject(WindowWidthService);
  private dialog = inject(MatDialog);
  private localStorageService = inject(LocalStorageService);
  private snackBar = inject(MatSnackBar);
  private deviceService = inject(DeviceService);
  private categoryService = inject(CategoryService);
  private locationService = inject(LocationService);
  private ownerService = inject(OwnerService);
  private route = inject(ActivatedRoute);
  private location = inject(Location);
  private adminAccessService = inject(AdminAccessService);
  private titleService = inject(Title);

  protected loading = true;
  protected canCreate = false;
  @ViewChild(MatTable) table!: MatTable<DeviceDetails>;
  @ViewChild('optionSearchInput') optionSearchInput?: ElementRef<HTMLInputElement>;
  @ViewChild(MatMenuTrigger) contextMenuTrigger!: MatMenuTrigger;
  @ViewChild('contextMenuAnchor') contextMenuAnchor!: ElementRef<HTMLElement>;

  protected contextMenuDevice: DeviceDetails | null = null;

  protected deviceSearchFormControl = new FormControl();
  private searchFilter = '';

  protected displayedColumns = ALL_COLUMNS;

  protected devices: DeviceDetails[] = [];
  protected filteredDevices: DeviceDetails[] = [];
  protected pagedDevices: DeviceDetails[] = [];

  private lastPageSetting: PageEvent | undefined;
  protected pageSize = 5;

  private lastSort: Sort = { active: 'name', direction: 'asc' };

  protected openFilterColumn: string | null = null;
  protected panelAlignRight = false;
  protected optionSearchText = '';
  private readonly filterPanelWidth = 320;

  protected categories: CategoryDetails[] = [];
  protected locations: LocationDetails[] = [];
  protected owners: OwnerDetailsDto[] = [];
  protected makers: string[] = [];
  protected models: string[] = [];

  protected visibleCategories: string[] = [];
  protected visibleLocations: string[] = [];
  protected visibleOwners: string[] = [];
  protected visibleMakers: string[] = [];
  protected visibleModels: string[] = [];

  protected selectedCategories = new Set<string>();
  protected selectedLocations = new Set<string>();
  protected selectedOwners = new Set<string>();
  protected selectedMakers = new Set<string>();
  protected selectedModels = new Set<string>();

  constructor() {
    this.titleService.setTitle('Raktr - Eszközök');

    effect(() => {
      const width = this.windowService.windowWidth();
      this.displayedColumns = width >= 1200 ? ALL_COLUMNS : REDUCED_COLUMNS;
    });
  }

  ngOnInit() {
    this.adminAccessService
      .canCreateContent()
      .subscribe((canCreate) => (this.canCreate = canCreate));

    const readPageSize = this.localStorageService.read(`${environment.defaultPageSizeKey}`);
    if (readPageSize) {
      this.pageSize = parseInt(readPageSize);
    }

    this.deviceService.getDevices().subscribe((devices) => {
      this.devices = devices;
      this.updateMakersAndModels();
      this.filterSortDevices();

      this.loading = false;

      const deviceId = this.route.snapshot.paramMap.get('id');
      if (deviceId === 'new') {
        this.newDevice();
      } else if (deviceId) {
        const device = this.devices.find((d) => d.id === +deviceId);
        if (device) {
          this.openDevice(device);
        } else {
          this.location.go('/inventory/devices');
          this.snackBar.open(`Nincs eszköz ${deviceId} azonosítóval!`, 'Kár :(', {
            duration: 4000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['error-snackbar'],
          });
        }
      }
    });

    this.categoryService.getCategories().subscribe((categories) => {
      this.categories = categories;
      this.updateVisibleFilterOptions();
    });
    this.locationService.getLocations().subscribe((locations) => {
      this.locations = locations;
      this.updateVisibleFilterOptions();
    });
    this.ownerService.getOwners().subscribe((owners) => {
      this.owners = owners;
      this.updateVisibleFilterOptions();
    });
  }

  private updateMakersAndModels() {
    this.makers = Array.from(
      new Set(
        this.devices.map((device) => device.manufacturer).filter((manufacturer) => !!manufacturer),
      ),
    ).sort((a, b) => a.localeCompare(b));

    this.models = Array.from(
      new Set(this.devices.map((device) => device.model).filter((model) => !!model)),
    ).sort((a, b) => a.localeCompare(b));
  }

  protected openDevice(row: DeviceDetails) {
    this.location.go(`/inventory/devices/${row.id}`);

    const viewDeviceDialog = this.dialog.open(TabbedEditModalComponent, {
      width: '60vw',
      maxWidth: '100vw',
      position: { top: '40px' },
      data: { kind: 'device', item: row } as TabbedEditModalData,
    });

    viewDeviceDialog.afterClosed().subscribe((result) => {
      this.location.go('/inventory/devices');

      if (result === 'edit') {
        const editDeviceDialog = this.dialog.open(DeviceEditDialogComponent, {
          width: '60vw',
          maxWidth: '100vw',
          position: { top: '40px' },
          data: { device: row },
        });

        editDeviceDialog.afterClosed().subscribe((result) => {
          if (result) {
            this.replaceById(this.pagedDevices, result);
            this.table.renderRows();

            this.snackBar.open(`${result.name} frissítve!`, 'Remek!', {
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
    this.location.go('/inventory/devices/new');

    const editDeviceDialog = this.dialog.open(DeviceEditDialogComponent, {
      width: '60vw',
      maxWidth: '100vw',
    });

    editDeviceDialog.afterClosed().subscribe((result) => {
      this.location.go('/inventory/devices');

      if (result) {
        this.devices.push(result);
        this.updateMakersAndModels();
        this.filterSortDevices();

        const snackBarRef = this.snackBar.open(`${result.name} létrehozva!`, 'Megnyitás', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['success-snackbar'],
        });

        snackBarRef.onAction().subscribe(() => this.openDevice(result));
      }
    });
  }

  protected onDocumentContextMenu(event: MouseEvent) {
    if (!this.canCreate) {
      return;
    }

    // A right-click's target is whatever was hit-tested first, which while our menu is open is
    // its CDK overlay backdrop (covering the whole viewport to catch left-clicks that close the
    // menu) rather than the row underneath - elementFromPoint re-does that hit-test on demand,
    // so the backdrop is made click-through just long enough to ask it what's really there.
    const backdrops = Array.from(document.querySelectorAll<HTMLElement>('.cdk-overlay-backdrop'));
    backdrops.forEach((backdrop) => (backdrop.style.pointerEvents = 'none'));
    const target = document.elementFromPoint(event.clientX, event.clientY);
    backdrops.forEach((backdrop) => (backdrop.style.pointerEvents = ''));

    const row =
      target instanceof HTMLElement ? target.closest<HTMLElement>('tr[data-device-row]') : null;
    if (!row) {
      return;
    }

    const device = this.pagedDevices.find((d) => d.id === Number(row.dataset['deviceRow']));
    if (!device) {
      return;
    }

    event.preventDefault();

    const reopen = () => {
      this.contextMenuAnchor.nativeElement.style.left = `${event.clientX}px`;
      this.contextMenuAnchor.nativeElement.style.top = `${event.clientY}px`;
      this.contextMenuDevice = device;
      this.contextMenuTrigger.openMenu();
    };

    if (this.contextMenuTrigger.menuOpen) {
      this.contextMenuTrigger.closeMenu();
      // MatMenu's close animation (or its built-in 200ms no-animation fallback, see
      // MatMenuTrigger._setIsOpen in @angular/material/menu) defers the actual overlay detach,
      // so reopening has to wait at least that long or it reuses the not-yet-detached overlay
      // and keeps showing at the previous position.
      setTimeout(reopen, 200);
    } else {
      reopen();
    }
  }

  protected duplicateDevice(device: DeviceDetails) {
    const editDeviceDialog = this.dialog.open(DeviceEditDialogComponent, {
      width: '60vw',
      maxWidth: '100vw',
      data: { duplicateFrom: device } as DeviceDialogData,
    });

    editDeviceDialog.afterClosed().subscribe((result) => {
      if (result) {
        this.devices.push(result);
        this.updateMakersAndModels();
        this.filterSortDevices();

        const snackBarRef = this.snackBar.open(`${result.name} létrehozva!`, 'Megnyitás', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['success-snackbar'],
        });

        snackBarRef.onAction().subscribe(() => this.openDevice(result));
      }
    });
  }

  protected deleteDevice(device: DeviceDetails) {
    const confirmDialog = this.dialog.open(YesnoModalComponent, {
      width: '20vw',
      minWidth: '350px',
      data: `Biztosan törölni szeretnéd a(z) "${device.name}" eszközt?`,
    });

    confirmDialog.afterClosed().subscribe((confirmed) => {
      if (!confirmed) {
        return;
      }

      this.deviceService.deleteDevice(device.id).subscribe((success) => {
        if (!success) {
          return;
        }

        this.devices = this.devices.filter((d) => d.id !== device.id);
        this.updateMakersAndModels();
        this.filterSortDevices();

        this.snackBar.open(`${device.name} törölve!`, 'Értem', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['success-snackbar'],
        });
      });
    });
  }

  protected applyFilter() {
    this.searchFilter = this.deviceSearchFormControl.value;

    this.filterSortDevices();
  }

  protected resetFilter() {
    this.deviceSearchFormControl.reset();
    this.searchFilter = '';
    this.filterSortDevices();
  }

  protected announceSortChange($event: Sort) {
    this.lastSort = $event;
    this.filterSortDevices();
  }

  protected filterSortDevices() {
    this.filteredDevices = this.devices.filter(
      (device) =>
        this.matchesSearch(device) &&
        (this.selectedCategories.size === 0 || this.selectedCategories.has(device.category)) &&
        (this.selectedLocations.size === 0 || this.selectedLocations.has(device.location)) &&
        (this.selectedOwners.size === 0 || this.selectedOwners.has(device.owner?.name ?? '')) &&
        (this.selectedMakers.size === 0 || this.selectedMakers.has(device.manufacturer)) &&
        (this.selectedModels.size === 0 || this.selectedModels.has(device.model)),
    );

    this.sortDevices();
    this.updateVisibleFilterOptions();

    if (this.lastPageSetting !== undefined) {
      this.pageDevices(this.lastPageSetting);
    } else {
      this.pagedDevices = this.filteredDevices.slice(0, this.pageSize);
    }
  }

  private sortDevices() {
    const { active, direction } = this.lastSort;
    const comparator = direction ? this.getSortComparator(active) : null;
    if (!comparator) {
      return;
    }

    this.filteredDevices = this.filteredDevices
      .slice()
      .sort((a, b) => (direction === 'asc' ? comparator(a, b) : -comparator(a, b)));
  }

  private getSortComparator(
    active: string,
  ): ((a: DeviceDetails, b: DeviceDetails) => number) | null {
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
    return (
      (device.name ?? '').toLowerCase().includes(search) ||
      (device.assetTag ?? '').toLowerCase().includes(search) ||
      (device.model ?? '').toLowerCase().includes(search)
    );
  }

  private devicesMatchingExcept(
    excludedFacet: 'category' | 'location' | 'owner' | 'maker' | 'model',
  ): DeviceDetails[] {
    return this.devices.filter(
      (device) =>
        this.matchesSearch(device) &&
        (excludedFacet === 'category' ||
          this.selectedCategories.size === 0 ||
          this.selectedCategories.has(device.category)) &&
        (excludedFacet === 'location' ||
          this.selectedLocations.size === 0 ||
          this.selectedLocations.has(device.location)) &&
        (excludedFacet === 'owner' ||
          this.selectedOwners.size === 0 ||
          this.selectedOwners.has(device.owner?.name ?? '')) &&
        (excludedFacet === 'maker' ||
          this.selectedMakers.size === 0 ||
          this.selectedMakers.has(device.manufacturer)) &&
        (excludedFacet === 'model' ||
          this.selectedModels.size === 0 ||
          this.selectedModels.has(device.model)),
    );
  }

  private updateVisibleFilterOptions() {
    const availableCategories = new Set(
      this.devicesMatchingExcept('category').map((device) => device.category),
    );
    this.visibleCategories = this.categories
      .map((category) => category.name)
      .filter((name) => availableCategories.has(name) || this.selectedCategories.has(name));

    const availableLocations = new Set(
      this.devicesMatchingExcept('location').map((device) => device.location),
    );
    this.visibleLocations = this.locations
      .map((location) => location.name)
      .filter((name) => availableLocations.has(name) || this.selectedLocations.has(name));

    const availableOwners = new Set(
      this.devicesMatchingExcept('owner').map((device) => device.owner?.name ?? ''),
    );
    this.visibleOwners = this.owners
      .map((owner) => owner.name)
      .filter((name) => availableOwners.has(name) || this.selectedOwners.has(name));

    const availableMakers = new Set(
      this.devicesMatchingExcept('maker').map((device) => device.manufacturer),
    );
    this.visibleMakers = this.makers.filter(
      (maker) => availableMakers.has(maker) || this.selectedMakers.has(maker),
    );

    const availableModels = new Set(
      this.devicesMatchingExcept('model').map((device) => device.model),
    );
    this.visibleModels = this.models.filter(
      (model) => availableModels.has(model) || this.selectedModels.has(model),
    );
  }

  protected pageDevices(pageEvent: PageEvent) {
    this.lastPageSetting = pageEvent;
    this.pageSize = pageEvent.pageSize;
    this.localStorageService.write(
      `${environment.defaultPageSizeKey}`,
      pageEvent.pageSize.toString(),
    );

    const startId = pageEvent.pageIndex * pageEvent.pageSize;
    const endId = startId + pageEvent.pageSize;

    this.pagedDevices = this.filteredDevices.slice(startId, endId);
  }

  protected replaceById<T extends { id: string | number }>(array: T[], newObject: T): void {
    const index = array.findIndex((item) => item.id === newObject.id);
    if (index !== -1) {
      array[index] = newObject;
    }
  }

  protected toggleColumnFilter(column: string, event: MouseEvent) {
    if (this.openFilterColumn === column) {
      this.openFilterColumn = null;
      return;
    }

    this.openFilterColumn = column;
    this.optionSearchText = '';

    const buttonRect = (event.currentTarget as HTMLElement).getBoundingClientRect();
    this.panelAlignRight = buttonRect.left + this.filterPanelWidth > window.innerWidth;

    setTimeout(() => this.optionSearchInput?.nativeElement.focus());
  }

  protected filterOptions(options: string[]): string[] {
    const search = this.optionSearchText.toLowerCase();
    return options.filter((option) => option.toLowerCase().includes(search));
  }

  protected activateFilteredOptions(options: string[], selectedValues: Set<string>) {
    for (const option of this.filterOptions(options)) {
      selectedValues.add(option);
    }

    this.filterSortDevices();
  }

  protected confirmFilterSelection(options: string[], selectedValues: Set<string>) {
    this.activateFilteredOptions(options, selectedValues);
    this.closeFilterPanel();
  }

  protected closeFilterPanel() {
    this.openFilterColumn = null;
  }

  protected toggleFilterValue(selectedValues: Set<string>, value: string) {
    if (selectedValues.has(value)) {
      selectedValues.delete(value);
    } else {
      selectedValues.add(value);
    }

    this.filterSortDevices();
  }

  protected clearFilterSet(selectedValues: Set<string>) {
    selectedValues.clear();
    this.filterSortDevices();
  }

  protected activeFilterChips(): { label: string; remove: () => void }[] {
    const chips: { label: string; remove: () => void }[] = [];

    if (this.searchFilter) {
      chips.push({ label: `Keresés: ${this.searchFilter}`, remove: () => this.resetFilter() });
    }
    for (const maker of this.selectedMakers) {
      chips.push({
        label: `Gyártó: ${maker}`,
        remove: () => this.toggleFilterValue(this.selectedMakers, maker),
      });
    }
    for (const category of this.selectedCategories) {
      chips.push({
        label: `Kategória: ${category}`,
        remove: () => this.toggleFilterValue(this.selectedCategories, category),
      });
    }
    for (const location of this.selectedLocations) {
      chips.push({
        label: `Tárolási hely: ${location}`,
        remove: () => this.toggleFilterValue(this.selectedLocations, location),
      });
    }
    for (const owner of this.selectedOwners) {
      chips.push({
        label: `Tulajdonos: ${owner}`,
        remove: () => this.toggleFilterValue(this.selectedOwners, owner),
      });
    }
    for (const model of this.selectedModels) {
      chips.push({
        label: `Típus: ${model}`,
        remove: () => this.toggleFilterValue(this.selectedModels, model),
      });
    }

    return chips;
  }

  protected hasActiveFilters(): boolean {
    return (
      this.selectedCategories.size > 0 ||
      this.selectedLocations.size > 0 ||
      this.selectedOwners.size > 0 ||
      this.selectedMakers.size > 0 ||
      this.selectedModels.size > 0 ||
      !!this.searchFilter
    );
  }

  protected clearFilters() {
    this.selectedCategories.clear();
    this.selectedLocations.clear();
    this.selectedOwners.clear();
    this.selectedMakers.clear();
    this.selectedModels.clear();
    this.resetFilter();
  }
}
