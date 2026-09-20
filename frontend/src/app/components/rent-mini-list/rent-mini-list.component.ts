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
import {RentDetails} from '../../model/rent/rentDetails';

const DISPLAYED_COLUMNS: string[] = ['status', 'destination', 'outDate', 'expectedReturnDate', 'actualReturnDate'];

@Component({
  selector: 'app-rent-mini-list',
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
  templateUrl: './rent-mini-list.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './rent-mini-list.component.scss',
})
export class RentMiniListComponent {
  private router = inject(Router);
  private dialog = inject(MatDialog);

  /** The host is responsible for fetching - this component only displays what it's given. */
  rents = input.required<RentDetails[]>();
  loading = input<boolean>(false);

  protected readonly displayedColumns = DISPLAYED_COLUMNS;

  protected openRent(rent: RentDetails) {
    // Navigating away to the rent editing page would otherwise leave this dialog (and any
    // dialog it's nested in) stacked on top of it - close them all first.
    this.dialog.closeAll();
    this.router.navigate(['/rents', rent.id]);
  }
}
