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
} from '@angular/material/table';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {RentService} from '../../../services/rent.service';
import {RentDetails} from '../../../model/rent/rentDetails';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {DatePipe, DecimalPipe} from '@angular/common';
import {MatSortModule, Sort} from '@angular/material/sort';
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatIcon} from '@angular/material/icon';
import {MatButton, MatFabButton, MatIconButton} from '@angular/material/button';
import {MatCard} from '@angular/material/card';
import {LocalStorageService} from '../../../services/localStorage.service';
import {environment} from '../../../../environments/environment';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {WindowWidthService} from '../../../services/windowWidth.service';
import {MatCheckbox} from '@angular/material/checkbox';

const ALL_COLUMNS: string[] = ['status', 'issuer', 'renter', 'destination', 'outDate', 'expectedReturnDate', 'actualReturnDate', 'itemCount', 'totalWeight'];
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
  ],
  templateUrl: './rents.component.html',
  styleUrl: './rents.component.scss',
})
export class RentsComponent implements OnInit {

  protected loading: boolean = true;
  @ViewChild(MatTable) table!: MatTable<RentDetails>;

  protected rentSearchFormControl = new FormControl();
  private searchFilter = '';

  protected displayedColumns = ALL_COLUMNS;

  protected rents: RentDetails[] = [];
  protected filteredRents: RentDetails[] = [];
  protected pagedRents: RentDetails[] = [];

  private lastPageSetting: PageEvent | undefined;
  protected pageSize = 5;

  private lastSort: Sort = {active: 'outDate', direction: 'desc'};

  protected filterPanelOpen = false;

  protected issuers: string[] = [];
  protected renters: string[] = [];

  protected visibleIssuers: string[] = [];
  protected visibleRenters: string[] = [];

  protected selectedIssuers = new Set<string>();
  protected selectedRenters = new Set<string>();

  constructor(
    private windowService: WindowWidthService,
    private localStorageService: LocalStorageService,
    private rentService: RentService,
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

    this.rentService.getRents().subscribe(rents => {
      this.rents = rents;
      this.updateFilterOptions();
      this.filterSortRents();

      this.loading = false;
    });
  }

  private updateFilterOptions() {
    this.issuers = Array.from(new Set(
      this.rents.map(rent => rent.issuer?.nickname).filter((nickname): nickname is string => !!nickname)
    )).sort((a, b) => a.localeCompare(b));

    this.renters = Array.from(new Set(
      this.rents.map(rent => rent.renterName).filter((renterName): renterName is string => !!renterName)
    )).sort((a, b) => a.localeCompare(b));
  }

  protected applyFilter($event: KeyboardEvent) {
    this.searchFilter = this.rentSearchFormControl.value;

    this.filterSortRents();
  }

  protected announceSortChange($event: Sort) {
    this.lastSort = $event;
    this.filterSortRents();
  }

  protected filterSortRents() {
    this.filteredRents = this.rents.filter(rent =>
      this.matchesSearch(rent) &&
      (this.selectedIssuers.size === 0 || this.selectedIssuers.has(rent.issuer?.nickname ?? '')) &&
      (this.selectedRenters.size === 0 || this.selectedRenters.has(rent.renterName ?? ''))
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
    const {active, direction} = this.lastSort;
    const comparator = direction ? this.getSortComparator(active) : null;
    if (!comparator) {
      return;
    }

    this.filteredRents = this.filteredRents.slice().sort((a, b) =>
      direction === 'asc' ? comparator(a, b) : -comparator(a, b));
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
    return (rent.destination ?? '').toLowerCase().includes(search) ||
      (rent.issuer?.nickname ?? '').toLowerCase().includes(search) ||
      (rent.renterName ?? '').toLowerCase().includes(search);
  }

  private rentsMatchingExcept(excludedFacet: 'issuer' | 'renter'): RentDetails[] {
    return this.rents.filter(rent =>
      this.matchesSearch(rent) &&
      (excludedFacet === 'issuer' || this.selectedIssuers.size === 0 || this.selectedIssuers.has(rent.issuer?.nickname ?? '')) &&
      (excludedFacet === 'renter' || this.selectedRenters.size === 0 || this.selectedRenters.has(rent.renterName ?? ''))
    );
  }

  private updateVisibleFilterOptions() {
    const availableIssuers = new Set(this.rentsMatchingExcept('issuer').map(rent => rent.issuer?.nickname ?? ''));
    this.visibleIssuers = this.issuers.filter(issuer =>
      availableIssuers.has(issuer) || this.selectedIssuers.has(issuer));

    const availableRenters = new Set(this.rentsMatchingExcept('renter').map(rent => rent.renterName ?? ''));
    this.visibleRenters = this.renters.filter(renter =>
      availableRenters.has(renter) || this.selectedRenters.has(renter));
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

    this.filterSortRents();
  }

  protected hasActiveFilters(): boolean {
    return this.selectedIssuers.size > 0 || this.selectedRenters.size > 0;
  }

  protected clearFilters() {
    this.selectedIssuers.clear();
    this.selectedRenters.clear();

    this.filterSortRents();
  }

  protected pageRents(pageEvent: PageEvent) {
    this.lastPageSetting = pageEvent;
    this.pageSize = pageEvent.pageSize;
    this.localStorageService.write(`${environment.defaultPageSizeKey}`, pageEvent.pageSize.toString());

    const startId = (pageEvent.pageIndex) * pageEvent.pageSize;
    const endId = startId + pageEvent.pageSize;

    this.pagedRents = this.filteredRents.slice(startId, endId);
  }

  protected resetFilter() {
    this.rentSearchFormControl.reset();
    this.searchFilter = '';
    this.filterSortRents();
  }
}
