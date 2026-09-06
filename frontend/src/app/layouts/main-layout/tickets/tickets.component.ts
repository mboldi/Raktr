import {Component, effect, OnInit} from '@angular/core';
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
import {TicketService} from '../../../services/ticket.service';
import {TicketDetails} from '../../../model/ticket/ticketDetails';
import {TicketStatus} from '../../../model/ticket/ticketStatus';
import {TicketSeverity} from '../../../model/ticket/ticketSeverity';
import {TICKET_SEVERITY_LABELS, TICKET_SEVERITY_ORDER, TICKET_STATUS_LABELS, TICKET_STATUS_ORDER} from '../../../model/ticket/ticketLabels';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {DatePipe} from '@angular/common';
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
import {MatSlideToggle} from '@angular/material/slide-toggle';
import {MatTooltip} from '@angular/material/tooltip';
import {MatDialog} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {TicketDialogResult, TicketEditDialogComponent} from '../../../components/ticket-edit-modal/ticket-edit-dialog.component';

const ALL_COLUMNS: string[] = ['severity', 'status', 'id', 'createdAt', 'device', 'description', 'createdBy', 'comments'];
const REDUCED_COLUMNS: string[] = ['severity', 'id', 'status', 'createdAt', 'device'];

@Component({
  selector: 'app-tickets',
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
    MatSlideToggle,
    MatTooltip,
  ],
  templateUrl: './tickets.component.html',
  styleUrl: './tickets.component.scss',
})
export class TicketsComponent implements OnInit {

  protected loading: boolean = true;

  protected ticketSearchFormControl = new FormControl();
  private searchFilter = '';

  protected displayedColumns = ALL_COLUMNS;

  protected tickets: TicketDetails[] = [];
  protected filteredTickets: TicketDetails[] = [];
  protected pagedTickets: TicketDetails[] = [];

  private lastPageSetting: PageEvent | undefined;
  protected pageSize = 5;

  private lastSort: Sort = {active: 'createdAt', direction: 'desc'};

  protected filterPanelOpen = false;

  protected statuses: TicketStatus[] = Object.values(TicketStatus);
  protected severities: TicketSeverity[] = Object.values(TicketSeverity);
  protected creators: string[] = [];

  protected visibleStatuses: TicketStatus[] = this.statuses;
  protected visibleSeverities: TicketSeverity[] = this.severities;
  protected visibleCreators: string[] = [];

  protected selectedStatuses = new Set<TicketStatus>();
  protected selectedSeverities = new Set<TicketSeverity>();
  protected selectedCreators = new Set<string>();

  protected showClosedTickets = false;

  protected readonly statusLabels = TICKET_STATUS_LABELS;
  protected readonly severityLabels = TICKET_SEVERITY_LABELS;

  protected statusLabel(status: TicketStatus): string {
    return TICKET_STATUS_LABELS[status];
  }

  protected severityLabel(severity: TicketSeverity): string {
    return TICKET_SEVERITY_LABELS[severity];
  }

  constructor(
    private windowService: WindowWidthService,
    private localStorageService: LocalStorageService,
    private ticketService: TicketService,
    private dialog: MatDialog,
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

    this.ticketService.getTickets().subscribe(tickets => {
      this.tickets = tickets;
      this.updateCreators();
      this.filterSortTickets();

      this.loading = false;
    });
  }

  private updateCreators() {
    this.creators = Array.from(new Set(
      this.tickets.map(ticket => ticket.createdBy?.nickname).filter((nickname): nickname is string => !!nickname)
    )).sort((a, b) => a.localeCompare(b));
  }

  protected applyFilter($event: KeyboardEvent) {
    this.searchFilter = this.ticketSearchFormControl.value;

    this.filterSortTickets();
  }

  protected announceSortChange($event: Sort) {
    this.lastSort = $event;
    this.filterSortTickets();
  }

  protected toggleShowClosedTickets(show: boolean) {
    this.showClosedTickets = show;
    this.filterSortTickets();
  }

  protected filterSortTickets() {
    this.filteredTickets = this.tickets.filter(ticket =>
      this.matchesSearch(ticket) &&
      (this.showClosedTickets || ticket.status !== TicketStatus.CLOSED) &&
      (this.selectedStatuses.size === 0 || this.selectedStatuses.has(ticket.status)) &&
      (this.selectedSeverities.size === 0 || this.selectedSeverities.has(ticket.severity)) &&
      (this.selectedCreators.size === 0 || this.selectedCreators.has(ticket.createdBy?.nickname ?? ''))
    );

    this.sortTickets();
    this.updateVisibleFilterOptions();

    if (this.lastPageSetting !== undefined) {
      this.pageTickets(this.lastPageSetting);
    } else {
      this.pagedTickets = this.filteredTickets.slice(0, this.pageSize);
    }
  }

  private sortTickets() {
    const {active, direction} = this.lastSort;
    const comparator = direction ? this.getSortComparator(active) : null;
    if (!comparator) {
      return;
    }

    this.filteredTickets = this.filteredTickets.slice().sort((a, b) =>
      direction === 'asc' ? comparator(a, b) : -comparator(a, b));
  }

  private getSortComparator(active: string): ((a: TicketDetails, b: TicketDetails) => number) | null {
    switch (active) {
      case 'id':
        return (a, b) => a.id - b.id;
      case 'severity':
        return (a, b) => TICKET_SEVERITY_ORDER[a.severity] - TICKET_SEVERITY_ORDER[b.severity];
      case 'status':
        return (a, b) => TICKET_STATUS_ORDER[a.status] - TICKET_STATUS_ORDER[b.status];
      case 'createdAt':
        return (a, b) => this.compareDates(a.createdAt, b.createdAt);
      case 'device':
        return (a, b) => this.compareStrings(a.scannable?.name, b.scannable?.name);
      case 'createdBy':
        return (a, b) => this.compareStrings(a.createdBy?.nickname, b.createdBy?.nickname);
      case 'comments':
        return (a, b) => a.comments.length - b.comments.length;
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

  private matchesSearch(ticket: TicketDetails): boolean {
    const search = this.searchFilter.toLowerCase();
    return (ticket.scannable?.name ?? '').toLowerCase().includes(search) ||
      (ticket.description ?? '').toLowerCase().includes(search);
  }

  private ticketsMatchingExcept(excludedFacet: 'status' | 'severity' | 'creator'): TicketDetails[] {
    return this.tickets.filter(ticket =>
      this.matchesSearch(ticket) &&
      (this.showClosedTickets || ticket.status !== TicketStatus.CLOSED) &&
      (excludedFacet === 'status' || this.selectedStatuses.size === 0 || this.selectedStatuses.has(ticket.status)) &&
      (excludedFacet === 'severity' || this.selectedSeverities.size === 0 || this.selectedSeverities.has(ticket.severity)) &&
      (excludedFacet === 'creator' || this.selectedCreators.size === 0 || this.selectedCreators.has(ticket.createdBy?.nickname ?? ''))
    );
  }

  private updateVisibleFilterOptions() {
    const availableStatuses = new Set(this.ticketsMatchingExcept('status').map(ticket => ticket.status));
    this.visibleStatuses = this.statuses.filter(status =>
      availableStatuses.has(status) || this.selectedStatuses.has(status));

    const availableSeverities = new Set(this.ticketsMatchingExcept('severity').map(ticket => ticket.severity));
    this.visibleSeverities = this.severities.filter(severity =>
      availableSeverities.has(severity) || this.selectedSeverities.has(severity));

    const availableCreators = new Set(this.ticketsMatchingExcept('creator').map(ticket => ticket.createdBy?.nickname ?? ''));
    this.visibleCreators = this.creators.filter(creator =>
      availableCreators.has(creator) || this.selectedCreators.has(creator));
  }

  protected pageTickets(pageEvent: PageEvent) {
    this.lastPageSetting = pageEvent;
    this.pageSize = pageEvent.pageSize;
    this.localStorageService.write(`${environment.defaultPageSizeKey}`, pageEvent.pageSize.toString());

    const startId = (pageEvent.pageIndex) * pageEvent.pageSize;
    const endId = startId + pageEvent.pageSize;

    this.pagedTickets = this.filteredTickets.slice(startId, endId);
  }

  protected resetFilter() {
    this.ticketSearchFormControl.reset();
    this.searchFilter = '';
    this.filterSortTickets();
  }

  protected toggleFilterPanel() {
    this.filterPanelOpen = !this.filterPanelOpen;
  }

  protected closeFilterPanel() {
    this.filterPanelOpen = false;
  }

  protected toggleFilterValue<T>(selectedValues: Set<T>, value: T) {
    if (selectedValues.has(value)) {
      selectedValues.delete(value);
    } else {
      selectedValues.add(value);
    }

    this.filterSortTickets();
  }

  protected hasActiveFilters(): boolean {
    return this.selectedStatuses.size > 0 || this.selectedSeverities.size > 0 || this.selectedCreators.size > 0;
  }

  protected clearFilters() {
    this.selectedStatuses.clear();
    this.selectedSeverities.clear();
    this.selectedCreators.clear();

    this.filterSortTickets();
  }

  protected newTicket() {
    const dialogRef = this.dialog.open(TicketEditDialogComponent, {
      width: '60vw',
      maxWidth: '100vw',
      position: {top: '40px'},
    });

    dialogRef.afterClosed().subscribe((response?: TicketDialogResult) => {
      if (response) {
        this.tickets.push(response.ticket);
        this.updateCreators();
        this.filterSortTickets();

        if (response.saved) {
          this.snackBar.open('Hibajegy létrehozva!', 'Remek!', {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['success-snackbar'],
          });
        }
      }
    });
  }

  protected openTicket(ticket: TicketDetails) {
    const dialogRef = this.dialog.open(TicketEditDialogComponent, {
      width: '60vw',
      maxWidth: '100vw',
      position: {top: '40px'},
      data: ticket,
    });

    dialogRef.afterClosed().subscribe((response?: TicketDialogResult) => {
      if (response) {
        this.replaceById(this.tickets, response.ticket);
        this.filterSortTickets();

        if (response.saved) {
          this.snackBar.open('Hibajegy frissítve!', 'Remek!', {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['success-snackbar'],
          });
        }
      }
    });
  }

  private replaceById<T extends { id: number }>(array: T[], newObject: T): void {
    const index = array.findIndex(item => item.id === newObject.id);
    if (index !== -1) {
      array[index] = newObject;
    }
  }
}
