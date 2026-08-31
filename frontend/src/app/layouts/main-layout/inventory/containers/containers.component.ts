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
import {ContainerService} from "../../../../services/container.service";
import {ContainerDetails} from "../../../../model/scannable/container/containerDetails";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {DecimalPipe} from "@angular/common";
import {MatSortModule, Sort} from "@angular/material/sort";
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {MatButton, MatFabButton, MatIconButton} from "@angular/material/button";
import {MatCard} from '@angular/material/card';
import {MatDialog} from '@angular/material/dialog';
import {LocalStorageService} from '../../../../services/localStorage.service';
import {environment} from '../../../../../environments/environment';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {
  TabbedEditModalComponent,
  TabbedEditModalData
} from '../../../../components/tabbed-edit-modal/tabbed-edit-modal.component';
import {ContainerEditDialogComponent} from '../../../../components/container-edit-modal/container-edit-dialog.component';
import {MatSnackBar} from '@angular/material/snack-bar';
import {WindowWidthService} from '../../../../services/windowWidth.service';
import {CategoryService} from '../../../../services/category.service';
import {LocationService} from '../../../../services/location.service';
import {OwnerService} from '../../../../services/owner.service';
import {CategoryDetails} from '../../../../model/category/categoryDetails';
import {LocationDetails} from '../../../../model/location/LocationDetails';
import {OwnerDetailsDto} from '../../../../model/owner/ownerDetailsDto';
import {MatCheckbox} from '@angular/material/checkbox';

const ALL_COLUMNS: string[] = ['name', 'assetTag', 'category', 'location', 'itemCount', 'totalWeight'];
const REDUCED_COLUMNS: string[] = ['name', 'assetTag', 'location', 'itemCount'];

@Component({
  selector: 'app-containers',
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
  templateUrl: './containers.component.html',
  styleUrl: './containers.component.scss',
})
export class ContainersComponent implements OnInit {

  protected loading: boolean = true;
  @ViewChild(MatTable) table!: MatTable<ContainerDetails>;

  protected containerSearchFormControl = new FormControl();
  private searchFilter = "";

  protected displayedColumns = ALL_COLUMNS;

  protected containers: ContainerDetails[] = [];
  protected filteredContainers: ContainerDetails[] = [];
  protected pagedContainers: ContainerDetails[] = [];

  private lastPageSetting: PageEvent | undefined;
  protected pageSize = 5;

  private lastSort: Sort = {active: 'name', direction: 'asc'};

  protected filterPanelOpen = false;

  protected categories: CategoryDetails[] = [];
  protected locations: LocationDetails[] = [];
  protected owners: OwnerDetailsDto[] = [];

  protected visibleCategories: CategoryDetails[] = [];
  protected visibleLocations: LocationDetails[] = [];
  protected visibleOwners: OwnerDetailsDto[] = [];

  protected selectedCategories = new Set<string>();
  protected selectedLocations = new Set<string>();
  protected selectedOwners = new Set<string>();

  constructor(
    private windowService: WindowWidthService,
    private dialog: MatDialog,
    private localStorageService: LocalStorageService,
    private containerService: ContainerService,
    private categoryService: CategoryService,
    private locationService: LocationService,
    private ownerService: OwnerService,
    private snackBar: MatSnackBar,
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

    this.containerService.getContainers().subscribe(containers => {
      this.containers = containers;
      this.filterSortContainers();

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

  protected openContainer(row: any) {
    const viewModal = this.dialog.open(TabbedEditModalComponent, {
      width: '50vw',
      maxWidth: '100vw',
      position: {top: '10px'},
      data: {kind: 'container', item: row} as TabbedEditModalData
    });

    viewModal.afterClosed().subscribe(result => {
      if (result === 'edit') {
        const editContainerDialog = this.dialog.open(ContainerEditDialogComponent, {
          width: '50vw',
          maxWidth: '100vw',
          data: row
        });

        editContainerDialog.afterClosed().subscribe(result => {
          if (result) {
            // The dialog itself reports success (or item add/remove feedback) via its own
            // snackbars, since it can close this way even without an actual save happening
            // (e.g. dismissed by clicking outside) - only sync the list here, silently.
            this.replaceById(this.containers, result);
            this.filterSortContainers();
            this.table.renderRows();
          }
        })
      }
    })
  }

  protected newContainer() {
    const editContainerDialog = this.dialog.open(ContainerEditDialogComponent, {
      width: '40vw',
      maxWidth: '100vw',
    });

    editContainerDialog.afterClosed().subscribe(result => {
      if (result) {
        this.containers.push(result);
        this.filterSortContainers();

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
    this.searchFilter = this.containerSearchFormControl.value;

    this.filterSortContainers();
  }

  protected announceSortChange($event: Sort) {
    this.lastSort = $event;
    this.filterSortContainers();
  }

  protected filterSortContainers() {
    this.filteredContainers = this.containers.filter(container =>
      this.matchesSearch(container) &&
      (this.selectedCategories.size === 0 || this.selectedCategories.has(container.category)) &&
      (this.selectedLocations.size === 0 || this.selectedLocations.has(container.location)) &&
      (this.selectedOwners.size === 0 || this.selectedOwners.has(container.owner?.name ?? ''))
    )

    this.sortContainers();
    this.updateVisibleFilterOptions();

    if (this.lastPageSetting !== undefined) {
      this.pageContainers(this.lastPageSetting);
    } else {
      this.pagedContainers = this.filteredContainers.slice(0, this.pageSize);
    }
  }

  private sortContainers() {
    const {active, direction} = this.lastSort;
    const comparator = direction ? this.getSortComparator(active) : null;
    if (!comparator) {
      return;
    }

    this.filteredContainers = this.filteredContainers.slice().sort((a, b) =>
      direction === 'asc' ? comparator(a, b) : -comparator(a, b));
  }

  private getSortComparator(active: string): ((a: ContainerDetails, b: ContainerDetails) => number) | null {
    switch (active) {
      case 'name':
        return (a, b) => this.compareStrings(a.name, b.name);
      case 'assetTag':
        return (a, b) => this.compareStrings(a.assetTag, b.assetTag);
      case 'itemCount':
        return (a, b) => a.getItemCount() - b.getItemCount();
      case 'totalWeight':
        return (a, b) => a.totalWeight - b.totalWeight;
      default:
        return null;
    }
  }

  private compareStrings(a: string | null | undefined, b: string | null | undefined): number {
    return (a ?? '').localeCompare(b ?? '');
  }

  private matchesSearch(container: ContainerDetails): boolean {
    const search = this.searchFilter.toLowerCase();
    return (container.name ?? '').toLowerCase().includes(search) ||
      (container.assetTag ?? '').toLowerCase().includes(search);
  }

  private containersMatchingExcept(excludedFacet: 'category' | 'location' | 'owner'): ContainerDetails[] {
    return this.containers.filter(container =>
      this.matchesSearch(container) &&
      (excludedFacet === 'category' || this.selectedCategories.size === 0 || this.selectedCategories.has(container.category)) &&
      (excludedFacet === 'location' || this.selectedLocations.size === 0 || this.selectedLocations.has(container.location)) &&
      (excludedFacet === 'owner' || this.selectedOwners.size === 0 || this.selectedOwners.has(container.owner?.name ?? ''))
    );
  }

  private updateVisibleFilterOptions() {
    const availableCategories = new Set(this.containersMatchingExcept('category').map(container => container.category));
    this.visibleCategories = this.categories.filter(category =>
      availableCategories.has(category.name) || this.selectedCategories.has(category.name));

    const availableLocations = new Set(this.containersMatchingExcept('location').map(container => container.location));
    this.visibleLocations = this.locations.filter(location =>
      availableLocations.has(location.name) || this.selectedLocations.has(location.name));

    const availableOwners = new Set(this.containersMatchingExcept('owner').map(container => container.owner?.name ?? ''));
    this.visibleOwners = this.owners.filter(owner =>
      availableOwners.has(owner.name) || this.selectedOwners.has(owner.name));
  }

  protected pageContainers(pageEvent: PageEvent) {
    this.lastPageSetting = pageEvent;
    this.pageSize = pageEvent.pageSize;
    this.localStorageService.write(`${environment.defaultPageSizeKey}`, pageEvent.pageSize.toString())

    const startId = (pageEvent.pageIndex) * pageEvent.pageSize;
    const endId = startId + pageEvent.pageSize;

    this.pagedContainers = this.filteredContainers.slice(startId, endId);
  }

  protected replaceById<T extends { id: string | number }>(array: T[], newObject: T): void {
    const index = array.findIndex(item => item.id === newObject.id);
    if (index !== -1) {
      array[index] = newObject;
    }
  }

  protected resetFilter() {
    this.containerSearchFormControl.reset();
    this.searchFilter = "";
    this.filterSortContainers();
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

    this.filterSortContainers();
  }

  protected hasActiveFilters(): boolean {
    return this.selectedCategories.size > 0 ||
      this.selectedLocations.size > 0 ||
      this.selectedOwners.size > 0;
  }

  protected clearFilters() {
    this.selectedCategories.clear();
    this.selectedLocations.clear();
    this.selectedOwners.clear();

    this.filterSortContainers();
  }
}
