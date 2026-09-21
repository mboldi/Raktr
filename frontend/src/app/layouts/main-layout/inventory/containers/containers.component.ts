import {
  Component,
  effect,
  ElementRef,
  OnInit,
  ViewChild,
  ChangeDetectionStrategy,
  inject,
} from '@angular/core';
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
import { ContainerService } from '../../../../services/container.service';
import { ContainerDetails } from '../../../../model/scannable/container/containerDetails';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { DecimalPipe } from '@angular/common';
import { MatSortModule, Sort } from '@angular/material/sort';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { MatButton, MatFabButton, MatIconButton } from '@angular/material/button';
import { MatCard } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { LocalStorageService } from '../../../../services/localStorage.service';
import { environment } from '../../../../../environments/environment';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import {
  TabbedEditModalComponent,
  TabbedEditModalData,
} from '../../../../components/tabbed-edit-modal/tabbed-edit-modal.component';
import { ContainerEditDialogComponent } from '../../../../components/container-edit-modal/container-edit-dialog.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { WindowWidthService } from '../../../../services/windowWidth.service';
import { CategoryService } from '../../../../services/category.service';
import { LocationService } from '../../../../services/location.service';
import { AdminAccessService } from '../../../../services/adminAccess.service';
import { CategoryDetails } from '../../../../model/category/categoryDetails';
import { LocationDetails } from '../../../../model/location/LocationDetails';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatChip, MatChipRemove, MatChipSet } from '@angular/material/chips';
import { MatTooltip } from '@angular/material/tooltip';
import { Location, NgTemplateOutlet } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

const ALL_COLUMNS: string[] = [
  'name',
  'assetTag',
  'category',
  'location',
  'itemCount',
  'totalWeight',
];
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
    MatButton,
    MatChip,
    MatChipRemove,
    MatChipSet,
    MatTooltip,
    NgTemplateOutlet,
  ],
  templateUrl: './containers.component.html',
  host: {
    '(document:keydown.escape)': 'closeFilterPanel()',
  },
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './containers.component.scss',
})
export class ContainersComponent implements OnInit {
  private windowService = inject(WindowWidthService);
  private dialog = inject(MatDialog);
  private localStorageService = inject(LocalStorageService);
  private containerService = inject(ContainerService);
  private categoryService = inject(CategoryService);
  private locationService = inject(LocationService);
  private snackBar = inject(MatSnackBar);
  private route = inject(ActivatedRoute);
  private location = inject(Location);
  private adminAccessService = inject(AdminAccessService);

  protected loading = true;
  protected canCreate = false;
  @ViewChild(MatTable) table!: MatTable<ContainerDetails>;
  @ViewChild('optionSearchInput') optionSearchInput?: ElementRef<HTMLInputElement>;

  protected containerSearchFormControl = new FormControl();
  private searchFilter = '';

  protected displayedColumns = ALL_COLUMNS;

  protected containers: ContainerDetails[] = [];
  protected filteredContainers: ContainerDetails[] = [];
  protected pagedContainers: ContainerDetails[] = [];

  private lastPageSetting: PageEvent | undefined;
  protected pageSize = 5;

  private lastSort: Sort = { active: 'name', direction: 'asc' };

  protected openFilterColumn: string | null = null;
  protected panelAlignRight = false;
  protected optionSearchText = '';
  private readonly filterPanelWidth = 320;

  protected categories: CategoryDetails[] = [];
  protected locations: LocationDetails[] = [];

  protected visibleCategories: string[] = [];
  protected visibleLocations: string[] = [];

  protected selectedCategories = new Set<string>();
  protected selectedLocations = new Set<string>();

  constructor() {
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

    this.containerService.getContainers().subscribe((containers) => {
      this.containers = containers;
      this.filterSortContainers();

      this.loading = false;

      const containerId = this.route.snapshot.paramMap.get('id');
      if (containerId === 'new') {
        this.newContainer();
      } else if (containerId) {
        const container = this.containers.find((c) => c.id === +containerId);
        if (container) {
          this.openContainer(container);
        } else {
          this.location.go('/inventory/containers');
          this.snackBar.open(`Nincs szállítóláda ${containerId} azonosítóval!`, 'Értem', {
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
  }

  protected openContainer(row: ContainerDetails) {
    this.location.go(`/inventory/containers/${row.id}`);

    const viewModal = this.dialog.open(TabbedEditModalComponent, {
      width: '50vw',
      maxWidth: '100vw',
      maxHeight: '95vh',
      position: { top: '20px' },
      data: { kind: 'container', item: row } as TabbedEditModalData,
    });

    viewModal.afterClosed().subscribe((result) => {
      this.location.go('/inventory/containers');

      if (result === 'edit') {
        const editContainerDialog = this.dialog.open(ContainerEditDialogComponent, {
          width: '50vw',
          maxWidth: '100vw',
          maxHeight: '95vh',
          position: { top: '20px' },
          data: row,
        });

        editContainerDialog.afterClosed().subscribe((result) => {
          if (result) {
            // The dialog itself reports success (or item add/remove feedback) via its own
            // snackbars, since it can close this way even without an actual save happening
            // (e.g. dismissed by clicking outside) - only sync the list here, silently.
            this.replaceById(this.containers, result);
            this.filterSortContainers();
            this.table.renderRows();
          }
        });
      }
    });
  }

  protected newContainer() {
    this.location.go('/inventory/containers/new');

    const editContainerDialog = this.dialog.open(ContainerEditDialogComponent, {
      width: '40vw',
      maxWidth: '100vw',
    });

    editContainerDialog.afterClosed().subscribe((result) => {
      this.location.go('/inventory/containers');

      if (result) {
        this.containers.push(result);
        this.filterSortContainers();

        const snackBarRef = this.snackBar.open(`${result.name} létrehozva!`, 'Megnyitás', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['success-snackbar'],
        });

        snackBarRef.onAction().subscribe(() => this.openContainer(result));
      }
    });
  }

  protected applyFilter() {
    this.searchFilter = this.containerSearchFormControl.value;

    this.filterSortContainers();
  }

  protected resetFilter() {
    this.containerSearchFormControl.reset();
    this.searchFilter = '';
    this.filterSortContainers();
  }

  protected announceSortChange($event: Sort) {
    this.lastSort = $event;
    this.filterSortContainers();
  }

  protected filterSortContainers() {
    this.filteredContainers = this.containers.filter(
      (container) =>
        this.matchesSearch(container) &&
        (this.selectedCategories.size === 0 || this.selectedCategories.has(container.category)) &&
        (this.selectedLocations.size === 0 || this.selectedLocations.has(container.location)),
    );

    this.sortContainers();
    this.updateVisibleFilterOptions();

    if (this.lastPageSetting !== undefined) {
      this.pageContainers(this.lastPageSetting);
    } else {
      this.pagedContainers = this.filteredContainers.slice(0, this.pageSize);
    }
  }

  private sortContainers() {
    const { active, direction } = this.lastSort;
    const comparator = direction ? this.getSortComparator(active) : null;
    if (!comparator) {
      return;
    }

    this.filteredContainers = this.filteredContainers
      .slice()
      .sort((a, b) => (direction === 'asc' ? comparator(a, b) : -comparator(a, b)));
  }

  private getSortComparator(
    active: string,
  ): ((a: ContainerDetails, b: ContainerDetails) => number) | null {
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
    return (
      (container.name ?? '').toLowerCase().includes(search) ||
      (container.assetTag ?? '').toLowerCase().includes(search)
    );
  }

  private containersMatchingExcept(excludedFacet: 'category' | 'location'): ContainerDetails[] {
    return this.containers.filter(
      (container) =>
        this.matchesSearch(container) &&
        (excludedFacet === 'category' ||
          this.selectedCategories.size === 0 ||
          this.selectedCategories.has(container.category)) &&
        (excludedFacet === 'location' ||
          this.selectedLocations.size === 0 ||
          this.selectedLocations.has(container.location)),
    );
  }

  private updateVisibleFilterOptions() {
    const availableCategories = new Set(
      this.containersMatchingExcept('category').map((container) => container.category),
    );
    this.visibleCategories = this.categories
      .map((category) => category.name)
      .filter((name) => availableCategories.has(name) || this.selectedCategories.has(name));

    const availableLocations = new Set(
      this.containersMatchingExcept('location').map((container) => container.location),
    );
    this.visibleLocations = this.locations
      .map((location) => location.name)
      .filter((name) => availableLocations.has(name) || this.selectedLocations.has(name));
  }

  protected pageContainers(pageEvent: PageEvent) {
    this.lastPageSetting = pageEvent;
    this.pageSize = pageEvent.pageSize;
    this.localStorageService.write(
      `${environment.defaultPageSizeKey}`,
      pageEvent.pageSize.toString(),
    );

    const startId = pageEvent.pageIndex * pageEvent.pageSize;
    const endId = startId + pageEvent.pageSize;

    this.pagedContainers = this.filteredContainers.slice(startId, endId);
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

    this.filterSortContainers();
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

    this.filterSortContainers();
  }

  protected clearFilterSet(selectedValues: Set<string>) {
    selectedValues.clear();
    this.filterSortContainers();
  }

  protected activeFilterChips(): { label: string; remove: () => void }[] {
    const chips: { label: string; remove: () => void }[] = [];

    if (this.searchFilter) {
      chips.push({ label: `Keresés: ${this.searchFilter}`, remove: () => this.resetFilter() });
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

    return chips;
  }

  protected hasActiveFilters(): boolean {
    return (
      this.selectedCategories.size > 0 || this.selectedLocations.size > 0 || !!this.searchFilter
    );
  }

  protected clearFilters() {
    this.selectedCategories.clear();
    this.selectedLocations.clear();
    this.resetFilter();
  }
}
