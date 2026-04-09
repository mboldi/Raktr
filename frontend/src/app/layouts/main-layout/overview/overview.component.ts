import {Component, effect} from '@angular/core';
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {MatFabButton, MatIconButton} from '@angular/material/button';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {ScannableService} from '../../../services/scannable.service';
import {RentService} from '../../../services/rent.service';
import {TicketService} from '../../../services/ticket.service';
import {RentDetailsDto} from '../../../model/rent/rentDetailsDto';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
  MatTable
} from '@angular/material/table';
import {DatePipe, DecimalPipe} from '@angular/common';
import {WindowWidthService} from '../../../services/windowWidth.service';
import {MatProgressSpinner} from '@angular/material/progress-spinner';

const ALL_COLUMNS: string[] = ['destination', 'issuer', 'renter', 'outDate', 'expectedReturnDate', 'itemCount', 'sumWeight'];
const REDUCED_COLUMNS: string[] = ['destination', 'issuer', 'renter', 'outDate', 'expectedReturnDate'];

@Component({
  selector: 'app-overview',
  imports: [
    MatCard,
    MatCardHeader,
    MatIcon,
    MatCardContent,
    MatFormField,
    MatLabel,
    MatInput,
    MatIconButton,
    MatSuffix,
    ReactiveFormsModule,
    MatFabButton,
    MatCardTitle,
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatCellDef,
    MatHeaderCellDef,
    MatHeaderRow,
    MatRow,
    MatRowDef,
    MatHeaderRowDef,
    DatePipe,
    DecimalPipe,
    MatProgressSpinner,
  ],
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.scss',
})
export class OverviewComponent {
  protected deviceSearchFormControl: FormControl = new FormControl();

  protected scannableCount: number = 0;
  protected ticketCount: number = 0;

  protected activeRents: RentDetailsDto[] = [];
  protected displayedColumns: string[] = ALL_COLUMNS;
  protected rents_loaded: boolean = false;

  constructor(
    private windowService: WindowWidthService,
    private scannableService: ScannableService,
    private rentService: RentService,
    private ticketService: TicketService,) {

    effect(() => {
      const width = this.windowService.windowWidth();
      this.displayedColumns = width >= 1200 ? ALL_COLUMNS : REDUCED_COLUMNS;
    });
  }

  ngOnInit() {
    this.scannableService.getScannablesCount().subscribe(
      count => this.scannableCount = count);

    this.rentService.getRents().subscribe(rents => {
      this.activeRents = rents.filter(rent => !rent.closed)
        .sort((a, b) => a.expectedReturnDate > b.expectedReturnDate ? 1 : -1);  // sorting from first expected to come back to last
      this.rents_loaded = true;
    });

    this.ticketService.getTicketCount().subscribe(
      ticketCount => this.ticketCount = ticketCount);
  }

  protected searchDevice() {
    this.deviceSearchFormControl.setValue("");
  }

  protected addRent() {

  }

  protected addDevice() {

  }

  protected addTicket() {

  }

  protected beforeNow(date: Date): boolean {
    return date.getDate() < new Date().getDate();
  }
}
