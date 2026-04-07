import {Component} from '@angular/core';
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {MatFabButton, MatIconButton} from '@angular/material/button';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {ScannableService} from '../../../services/scannable.service';
import {RentService} from '../../../services/rent.service';
import {TicketService} from '../../../services/ticket.service';

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
  ],
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.scss',
})
export class OverviewComponent {
  protected deviceSearchFormControl: FormControl = new FormControl();

  protected scannableCount: number = 0;
  protected rentCount: number = 0;
  protected ticketCount: number = 0;

  constructor(
    private scannableService: ScannableService,
    private rentService: RentService,
    private ticketService: TicketService,) {
  }

  ngOnInit() {
    this.scannableService.getScannablesCount().subscribe(
      count => this.scannableCount = count);

    this.rentService.getRentCount().subscribe(
      rentCount => this.rentCount = rentCount);

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
}
