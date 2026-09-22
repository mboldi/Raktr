import { Component, effect, ChangeDetectionStrategy, inject, OnInit } from '@angular/core';
import { MatCard, MatCardContent, MatCardHeader, MatCardTitle } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { MatFormField, MatInput, MatLabel, MatSuffix } from '@angular/material/input';
import { MatFabButton, MatIconButton } from '@angular/material/button';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatAutocomplete, MatAutocompleteTrigger, MatOption } from '@angular/material/autocomplete';
import { ScannableService } from '../../../services/scannable.service';
import { RentService } from '../../../services/rent.service';
import { TicketService } from '../../../services/ticket.service';
import { DeviceService } from '../../../services/device.service';
import { ContainerService } from '../../../services/container.service';
import { AdminAccessService } from '../../../services/adminAccess.service';
import { RentDetails } from '../../../model/rent/rentDetails';
import { ScannableDetailsDto } from '../../../model/scannable/scannableDetailsDto';
import { DeviceDetails } from '../../../model/scannable/device/deviceDetails';
import { ContainerDetails } from '../../../model/scannable/container/containerDetails';
import { findByBarcode } from '../../../util/ean8';
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
import { AsyncPipe, DatePipe, DecimalPipe } from '@angular/common';
import { WindowWidthService } from '../../../services/windowWidth.service';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import {
  DeviceDialogData,
  DeviceEditDialogComponent,
} from '../../../components/device-edit-modal/device-edit-dialog.component';
import {
  TicketDialogResult,
  TicketEditDialogComponent,
} from '../../../components/ticket-edit-modal/ticket-edit-dialog.component';
import {
  TabbedEditModalComponent,
  TabbedEditModalData,
} from '../../../components/tabbed-edit-modal/tabbed-edit-modal.component';
import { YesnoModalComponent } from '../../../components/yesno-modal/yesno-modal.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { map, Observable, startWith } from 'rxjs';
import { Router } from '@angular/router';

const ALL_COLUMNS: string[] = [
  'destination',
  'issuer',
  'renter',
  'outDate',
  'expectedReturnDate',
  'itemCount',
  'sumWeight',
];
const REDUCED_COLUMNS: string[] = [
  'destination',
  'issuer',
  'renter',
  'outDate',
  'expectedReturnDate',
];

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
    AsyncPipe,
    MatProgressSpinner,
    MatAutocomplete,
    MatAutocompleteTrigger,
    MatOption,
  ],
  templateUrl: './overview.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './overview.component.scss',
})
export class OverviewComponent implements OnInit {
  private windowService = inject(WindowWidthService);
  private scannableService = inject(ScannableService);
  private rentService = inject(RentService);
  private ticketService = inject(TicketService);
  private deviceService = inject(DeviceService);
  private containerService = inject(ContainerService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);
  private router = inject(Router);
  private adminAccessService = inject(AdminAccessService);

  protected canCreate = false;

  protected deviceSearchFormControl: FormControl = new FormControl();
  protected filteredSearchOptions: Observable<ScannableDetailsDto[]>;

  protected scannableCount = 0;
  protected ticketCount = 0;

  protected activeRents: RentDetails[] = [];
  protected displayedColumns: string[] = ALL_COLUMNS;
  protected rents_loaded = false;

  private devices: DeviceDetails[] = [];
  private containers: ContainerDetails[] = [];

  constructor() {
    effect(() => {
      const width = this.windowService.windowWidth();
      this.displayedColumns = width >= 1200 ? ALL_COLUMNS : REDUCED_COLUMNS;
    });

    this.filteredSearchOptions = this.deviceSearchFormControl.valueChanges.pipe(
      startWith(''),
      map((value) => this.filterScannables(value || '')),
    );
  }

  ngOnInit() {
    this.adminAccessService
      .canCreateContent()
      .subscribe((canCreate) => (this.canCreate = canCreate));

    this.getScannables();

    this.deviceService.getDevices().subscribe((devices) => (this.devices = devices));
    this.containerService.getContainers().subscribe((containers) => (this.containers = containers));

    this.rentService.getRents().subscribe((rents) => {
      this.activeRents = rents
        .filter((rent) => !rent.closed)
        .sort((a, b) => (a.expectedReturnDate > b.expectedReturnDate ? 1 : -1)); // sorting from first expected to come back to last
      this.rents_loaded = true;
    });

    this.ticketService
      .getTicketCount()
      .subscribe((ticketCount) => (this.ticketCount = ticketCount));
  }

  private get scannables(): ScannableDetailsDto[] {
    return [...this.devices, ...this.containers];
  }

  private filterScannables(value: string): ScannableDetailsDto[] {
    const filter = value.toLowerCase();
    if (filter.length < 2) {
      return [];
    }

    const deviceMatches = this.devices.filter(
      (device) =>
        device.name.toLowerCase().includes(filter) ||
        device.assetTag.toLowerCase().includes(filter) ||
        (device.manufacturer ?? '').toLowerCase().includes(filter) ||
        (device.model ?? '').toLowerCase().includes(filter),
    );

    const containerMatches = this.containers.filter(
      (container) =>
        container.name.toLowerCase().includes(filter) ||
        container.assetTag.toLowerCase().includes(filter),
    );

    return [...deviceMatches, ...containerMatches].slice(0, 5);
  }

  // MatAutocomplete's Enter-to-select handling runs on keydown and fires (optionSelected),
  // but the same keypress still produces a native keyup afterward that also hits our own
  // (keyup.enter) handler - this flag stops that from opening it a second time.
  private justSelectedFromDropdown = false;

  protected onScannableOptionSelected() {
    this.justSelectedFromDropdown = true;
    this.searchDevice();
  }

  protected onSearchEnterKey() {
    if (this.justSelectedFromDropdown) {
      this.justSelectedFromDropdown = false;
      return;
    }

    this.searchDevice();
  }

  protected searchDevice() {
    const barcode = this.deviceSearchFormControl.value;
    if (!barcode) {
      return;
    }

    const matched = findByBarcode(this.scannables, (scannable) => scannable.barcode, barcode);

    if (matched) {
      this.dialog.open(TabbedEditModalComponent, {
        width: '60vw',
        maxWidth: '100vw',
        position: { top: '40px' },
        data: { kind: 'scannable', item: matched } as TabbedEditModalData,
      });
    } else if (this.canCreate) {
      this.offerCreateDevice(barcode);
    }

    this.deviceSearchFormControl.setValue('');
  }

  private offerCreateDevice(barcode: string) {
    const confirmDialog = this.dialog.open(YesnoModalComponent, {
      width: '20vw',
      minWidth: '350px',
      data: `Nem található eszköz "${barcode}" vonalkóddal. Szeretnél létrehozni egy újat?`,
    });

    confirmDialog.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.addDevice(barcode);
      }
    });
  }

  protected addRent() {
    this.router.navigate(['/rents', 'new']);
  }

  protected openRent(rent: RentDetails) {
    this.router.navigate(['/rents', rent.id]);
  }

  protected addDevice(presetBarcode?: string) {
    const addDeviceDialog = this.dialog.open(DeviceEditDialogComponent, {
      width: '60vw',
      maxWidth: '100vw',
      data: { presetBarcode } as DeviceDialogData,
    });

    addDeviceDialog.afterClosed().subscribe((result) => {
      if (result) {
        this.snackBar.open(`Eszköz hozzáadva: ${result.name}`, 'Oh yeah!', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['success-snackbar'],
        });

        this.getScannables();
      }
    });
  }

  protected addTicket() {
    const addTicketDialog = this.dialog.open(TicketEditDialogComponent, {
      width: '60vw',
      maxWidth: '100vw',
      position: { top: '40px' },
    });

    addTicketDialog.afterClosed().subscribe((response?: TicketDialogResult) => {
      if (response?.saved) {
        this.snackBar.open('Hibajegy létrehozva!', 'Remek!', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['success-snackbar'],
        });

        this.ticketService
          .getTicketCount()
          .subscribe((ticketCount) => (this.ticketCount = ticketCount));
      }
    });
  }

  protected beforeNow(date: Date): boolean {
    return date.getDate() < new Date().getDate();
  }

  private getScannables() {
    this.scannableService.getScannablesCount().subscribe((count) => (this.scannableCount = count));
  }
}
