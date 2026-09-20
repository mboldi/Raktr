import { Component, input, ChangeDetectionStrategy, inject } from '@angular/core';
import {Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
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
import {DatePipe} from '@angular/common';
import {MatIcon} from '@angular/material/icon';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatTooltip} from '@angular/material/tooltip';
import {TicketDetails} from '../../model/ticket/ticketDetails';
import {TicketStatus} from '../../model/ticket/ticketStatus';
import {TicketSeverity} from '../../model/ticket/ticketSeverity';
import {TICKET_SEVERITY_LABELS, TICKET_STATUS_LABELS} from '../../model/ticket/ticketLabels';

const DISPLAYED_COLUMNS: string[] = ['status', 'severity', 'createdAt', 'description'];

@Component({
  selector: 'app-ticket-mini-list',
  imports: [
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatCellDef,
    MatHeaderCellDef,
    MatHeaderRow,
    MatRow,
    MatHeaderRowDef,
    MatRowDef,
    DatePipe,
    MatIcon,
    MatProgressSpinner,
    MatTooltip,
  ],
  templateUrl: './ticket-mini-list.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './ticket-mini-list.component.scss',
})
export class TicketMiniListComponent {
  private router = inject(Router);
  private dialog = inject(MatDialog);

  /** The host is responsible for fetching - this component only displays what it's given, so
   * it doesn't depend on the tab it lives in actually being selected (mat-tab-group only
   * instantiates a tab's content once selected, which would otherwise delay the fetch). */
  tickets = input.required<TicketDetails[]>();
  loading = input<boolean>(false);

  protected readonly displayedColumns = DISPLAYED_COLUMNS;

  protected openTicket(ticket: TicketDetails) {
    // Navigating away to the ticket editing route would otherwise leave this dialog (and any
    // dialog it's nested in) stacked on top of the tickets page - close them all first.
    this.dialog.closeAll();
    this.router.navigate(['/tickets', ticket.id]);
  }

  protected statusLabel(status: TicketStatus): string {
    return TICKET_STATUS_LABELS[status];
  }

  protected severityLabel(severity: TicketSeverity): string {
    return TICKET_SEVERITY_LABELS[severity];
  }
}
