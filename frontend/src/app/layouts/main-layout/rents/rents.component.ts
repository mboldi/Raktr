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
import { RentService } from '../../../services/rent.service';
import { RentDetails } from '../../../model/rent/rentDetails';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { DatePipe, DecimalPipe, NgTemplateOutlet } from '@angular/common';
import { MatSortModule, Sort } from '@angular/material/sort';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIcon } from '@angular/material/icon';
import { MatButton, MatFabButton, MatIconButton } from '@angular/material/button';
import { MatCard } from '@angular/material/card';
import { LocalStorageService } from '../../../services/localStorage.service';
import { environment } from '../../../../environments/environment';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { WindowWidthService } from '../../../services/windowWidth.service';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatChip, MatChipRemove, MatChipSet } from '@angular/material/chips';
import { MatTooltip } from '@angular/material/tooltip';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { Router } from '@angular/router';

const ALL_COLUMNS: string[] = [
  'status',
  'issuer',
  'renter',
  'destination',
  'outDate',
  'expectedReturnDate',
  'actualReturnDate',
  'itemCount',
  'totalWeight',
];
const REDUCED_COLUMNS: string[] = ['status', 'issuer', 'renter', 'destination', 'outDate'];

@Component({
  selector: 'app-rents',
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
    DatePipe,
    DecimalPipe,
    MatSortModule,
    FormsModule,
    MatIcon,
    MatIconButton,
    MatSuffix,
    ReactiveFormsModule,
    MatCard,
    MatProgressSpinner,
    MatFabButton,
    MatButton,
    MatCheckbox,
    MatChip,
    MatChipRemove,
    MatChipSet,
    MatTooltip,
    MatSlideToggle,
    NgTemplateOutlet,
  ],
  templateUrl: './rents.component.html',
  host: {
    '(document:keydown.escape)': 'closeFilterPanel()',
  },
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './rents.component.scss',
})
export class RentsComponent implements OnInit {
  private windowService = inject(WindowWidthService);
  private localStorageService = inject(LocalStorageService);
  private rentService = inject(RentService);
  private router = inject(Router);

  protected loading = true;
  @ViewChild(MatTable) table!: MatTable<RentDetails>;
  @ViewChild('optionSearchInput') optionSearchInput?: ElementRef<HTMLInputElement>;

  protected rentSearchFormControl = new FormControl();
  private searchFilter = '';

  protected displayedColumns = ALL_COLUMNS;

  protected rents: RentDetails[] = [];
  protected filteredRents: RentDetails[] = [];
  protected pagedRents: RentDetails[] = [];

  private lastPageSetting: PageEvent | undefined;
  protected pageSize = 5;

  private lastSort: Sort = { active: 'outDate', direction: 'desc' };

  protected openFilterColumn: string | null = null;
  protected panelAlignRight = false;
  protected optionSearchText = '';
  private readonly filterPanelWidth = 320;

  protected issuers: string[] = [];
  protected renters: string[] = [];

  protected visibleIssuers: string[] = [];
  protected visibleRenters: string[] = [];

  protected selectedIssuers = new Set<string>();
  protected selectedRenters = new Set<string>();

  protected showClosedRents = false;

  constructor() {
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

    this.rentService.getRents().subscribe((rents) => {
      this.rents = rents;
      this.updateFilterOptions();
      this.filterSortRents();

      this.loading = false;
    });
  }

  private updateFilterOptions() {
    this.issuers = Array.from(
      new Set(
        this.rents
          .map((rent) => rent.issuer?.displayName)
          .filter((nickname): nickname is string => !!nickname),
      ),
    ).sort((a, b) => a.localeCompare(b));

    this.renters = Array.from(
      new Set(
        this.rents
          .map((rent) => rent.renterName)
          .filter((renterName): renterName is string => !!renterName),
      ),
    ).sort((a, b) => a.localeCompare(b));
  }

  protected applyFilter() {
    this.searchFilter = this.rentSearchFormControl.value;

    this.filterSortRents();
  }

  protected resetFilter() {
    this.rentSearchFormControl.reset();
    this.searchFilter = '';
    this.filterSortRents();
  }

  protected announceSortChange($event: Sort) {
    this.lastSort = $event;
    this.filterSortRents();
  }

  protected toggleShowClosedRents(show: boolean) {
    this.showClosedRents = show;
    this.filterSortRents();
  }

  protected filterSortRents() {
    this.filteredRents = this.rents.filter(
      (rent) =>
        this.matchesSearch(rent) &&
        (this.showClosedRents || !rent.closed) &&
        (this.selectedIssuers.size === 0 ||
          this.selectedIssuers.has(rent.issuer?.displayName ?? '')) &&
        (this.selectedRenters.size === 0 || this.selectedRenters.has(rent.renterName ?? '')),
    );

    this.sortRents();
    this.updateVisibleFilterOptions();

    if (this.lastPageSetting !== undefined) {
      this.pageRents(this.lastPageSetting);
    } else {
      this.pagedRents = this.filteredRents.slice(0, this.pageSize);
    }
  }

  private sortRents() {
    const { active, direction } = this.lastSort;
    const comparator = direction ? this.getSortComparator(active) : null;
    if (!comparator) {
      return;
    }

    this.filteredRents = this.filteredRents
      .slice()
      .sort((a, b) => (direction === 'asc' ? comparator(a, b) : -comparator(a, b)));
  }

  private getSortComparator(active: string): ((a: RentDetails, b: RentDetails) => number) | null {
    switch (active) {
      case 'destination':
        return (a, b) => this.compareStrings(a.destination, b.destination);
      case 'outDate':
        return (a, b) => this.compareDates(a.outDate, b.outDate);
      case 'expectedReturnDate':
        return (a, b) => this.compareDates(a.expectedReturnDate, b.expectedReturnDate);
      case 'itemCount':
        return (a, b) => a.rentItems.length - b.rentItems.length;
      case 'totalWeight':
        return (a, b) => a.getSumWeight() - b.getSumWeight();
      default:
        return null;
    }
  }

  private compareStrings(a: string | null | undefined, b: string | null | undefined): number {
    return (a ?? '').localeCompare(b ?? '');
  }

  private compareDates(a: Date | null | undefined, b: Date | null | undefined): number {
    return (a ? a.getTime() : 0) - (b ? b.getTime() : 0);
  }

  private matchesSearch(rent: RentDetails): boolean {
    const search = this.searchFilter.toLowerCase();
    return (
      (rent.destination ?? '').toLowerCase().includes(search) ||
      (rent.issuer?.displayName ?? '').toLowerCase().includes(search) ||
      (rent.renterName ?? '').toLowerCase().includes(search)
    );
  }

  private rentsMatchingExcept(excludedFacet: 'issuer' | 'renter'): RentDetails[] {
    return this.rents.filter(
      (rent) =>
        this.matchesSearch(rent) &&
        (this.showClosedRents || !rent.closed) &&
        (excludedFacet === 'issuer' ||
          this.selectedIssuers.size === 0 ||
          this.selectedIssuers.has(rent.issuer?.displayName ?? '')) &&
        (excludedFacet === 'renter' ||
          this.selectedRenters.size === 0 ||
          this.selectedRenters.has(rent.renterName ?? '')),
    );
  }

  private updateVisibleFilterOptions() {
    const availableIssuers = new Set(
      this.rentsMatchingExcept('issuer').map((rent) => rent.issuer?.displayName ?? ''),
    );
    this.visibleIssuers = this.issuers.filter(
      (issuer) => availableIssuers.has(issuer) || this.selectedIssuers.has(issuer),
    );

    const availableRenters = new Set(
      this.rentsMatchingExcept('renter').map((rent) => rent.renterName ?? ''),
    );
    this.visibleRenters = this.renters.filter(
      (renter) => availableRenters.has(renter) || this.selectedRenters.has(renter),
    );
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

    this.filterSortRents();
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

    this.filterSortRents();
  }

  protected clearFilterSet(selectedValues: Set<string>) {
    selectedValues.clear();
    this.filterSortRents();
  }

  protected activeFilterChips(): { label: string; remove: () => void }[] {
    const chips: { label: string; remove: () => void }[] = [];

    if (this.searchFilter) {
      chips.push({ label: `Keresés: ${this.searchFilter}`, remove: () => this.resetFilter() });
    }
    for (const issuer of this.selectedIssuers) {
      chips.push({
        label: `Kiadó: ${issuer}`,
        remove: () => this.toggleFilterValue(this.selectedIssuers, issuer),
      });
    }
    for (const renter of this.selectedRenters) {
      chips.push({
        label: `Felelősségvállaló: ${renter}`,
        remove: () => this.toggleFilterValue(this.selectedRenters, renter),
      });
    }

    return chips;
  }

  protected hasActiveFilters(): boolean {
    return this.selectedIssuers.size > 0 || this.selectedRenters.size > 0 || !!this.searchFilter;
  }

  protected clearFilters() {
    this.selectedIssuers.clear();
    this.selectedRenters.clear();
    this.resetFilter();
  }

  protected pageRents(pageEvent: PageEvent) {
    this.lastPageSetting = pageEvent;
    this.pageSize = pageEvent.pageSize;
    this.localStorageService.write(
      `${environment.defaultPageSizeKey}`,
      pageEvent.pageSize.toString(),
    );

    const startId = pageEvent.pageIndex * pageEvent.pageSize;
    const endId = startId + pageEvent.pageSize;

    this.pagedRents = this.filteredRents.slice(startId, endId);
  }

  protected newRent() {
    this.router.navigate(['/rents', 'new']);
  }

  protected openRent(rent: RentDetails) {
    this.router.navigate(['/rents', rent.id]);
  }
}
