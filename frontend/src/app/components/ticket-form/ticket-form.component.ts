import {ChangeDetectionStrategy, ChangeDetectorRef, Component, effect, input, OnInit, output} from '@angular/core';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {FormBuilder, FormControl, ReactiveFormsModule, UntypedFormGroup, Validators} from '@angular/forms';
import {MatSelect} from '@angular/material/select';
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from '@angular/material/autocomplete';
import {MatIcon} from '@angular/material/icon';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatCard, MatCardContent} from '@angular/material/card';
import {AsyncPipe} from '@angular/common';
import {map, Observable, startWith} from 'rxjs';
import {TicketDetails} from '../../model/ticket/ticketDetails';
import {TicketSeverity} from '../../model/ticket/ticketSeverity';
import {TicketStatus} from '../../model/ticket/ticketStatus';
import {Scannable} from '../../model/scannable/scannable';
import {DeviceDetails} from '../../model/scannable/device/deviceDetails';
import {DeviceService} from '../../services/device.service';

@Component({
  selector: 'app-ticket-form',
  imports: [
    MatFormField,
    MatLabel,
    MatInput,
    MatSuffix,
    ReactiveFormsModule,
    MatSelect,
    MatOption,
    MatIcon,
    MatIconButton,
    MatButton,
    MatCard,
    MatCardContent,
    MatAutocomplete,
    MatAutocompleteTrigger,
    AsyncPipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './ticket-form.component.html',
  styleUrl: './ticket-form.component.scss',
})
export class TicketFormComponent implements OnInit {
  /** Pass an existing ticket to pre-populate the form, or leave null for a blank create form. */
  ticketData = input<TicketDetails | null>(null);

  /** Disables every field, e.g. because the ticket is closed. */
  disabled = input<boolean>(false);

  /** Emits the latest raw form value whenever the user makes a change. */
  formChanged = output<Partial<TicketDetails>>();
  /** Emitted when the entered value doesn't match any known device's barcode. */
  deviceNotFound = output<void>();

  ticketForm: UntypedFormGroup;

  protected readonly TicketSeverity = TicketSeverity;
  protected readonly TicketStatus = TicketStatus;

  protected addDeviceFormControl = new FormControl('');
  protected devices: DeviceDetails[] = [];
  protected filteredDeviceOptions: Observable<DeviceDetails[]>;
  protected selectedDevice: DeviceDetails | null = null;

  protected existingScannable: Scannable | null = null;
  protected deviceModel: string | null = null;
  protected deviceSerialNumber: string | null = null;

  constructor(
    private fb: FormBuilder,
    private deviceService: DeviceService,
    private cdr: ChangeDetectorRef,
  ) {
    this.ticketForm = this.fb.group({
      description: ['', Validators.required],
      severity: [TicketSeverity.MINOR, Validators.required],
      status: [TicketStatus.OPEN, Validators.required],
    });

    effect(() => {
      if (this.disabled()) {
        this.ticketForm.disable({emitEvent: false});
      } else {
        this.ticketForm.enable({emitEvent: false});
      }
    });

    // Keeps the status dropdown in sync when the dialog updates the ticket out from under
    // this form - e.g. the automatic OPEN -> IN_PROGRESS transition after a new comment.
    effect(() => {
      const data = this.ticketData();
      if (data !== null) {
        const statusControl = this.ticketForm.get('status')!;
        if (statusControl.value !== data.status) {
          statusControl.setValue(data.status, {emitEvent: false});
        }
      }
    });

    this.filteredDeviceOptions = this.addDeviceFormControl.valueChanges.pipe(
      startWith(''),
      map(value => this.filterDevices(value || ''))
    );
  }

  protected get isNew(): boolean {
    return this.ticketData() === null;
  }

  ngOnInit(): void {
    const data = this.ticketData();
    if (data !== null) {
      this.ticketForm.patchValue(data);
      this.existingScannable = data.scannable;
      this.loadDeviceExtras(data.scannable.id);
    } else {
      this.deviceService.getDevices().subscribe(devices => {
        this.devices = devices;
        this.addDeviceFormControl.updateValueAndValidity();
      });
    }

    this.ticketForm.valueChanges.subscribe(value => this.formChanged.emit(value));
  }

  private filterDevices(value: string): DeviceDetails[] {
    const filter = value.toLowerCase();
    if (filter.length < 2) {
      return [];
    }

    return this.devices.filter(device =>
      device.name.toLowerCase().includes(filter) ||
      (device.model ?? '').toLowerCase().includes(filter) ||
      (device.manufacturer ?? '').toLowerCase().includes(filter)
    ).slice(0, 5);
  }

  // MatAutocomplete's Enter-to-select handling runs on keydown and fires (optionSelected),
  // but the same keypress still produces a native keyup afterward that also hits our own
  // (keyup.enter) handler - this flag stops that from resolving the device a second time.
  private justSelectedDeviceFromDropdown = false;

  protected onDeviceOptionSelected() {
    this.justSelectedDeviceFromDropdown = true;
    this.resolveSelectedDevice();
  }

  protected onSearchEnterKey() {
    if (this.justSelectedDeviceFromDropdown) {
      this.justSelectedDeviceFromDropdown = false;
      return;
    }

    this.resolveSelectedDevice();
  }

  protected resolveSelectedDevice() {
    const enteredValue: string = this.addDeviceFormControl.value ?? '';
    if (!enteredValue) {
      return;
    }

    const matchedDevice = this.devices.find(device => device.barcode === enteredValue);
    if (!matchedDevice) {
      this.deviceNotFound.emit();
      return;
    }

    this.selectedDevice = matchedDevice;
    this.addDeviceFormControl.reset();
  }

  protected clearSelectedDevice() {
    this.selectedDevice = null;
  }

  /** The ticket's scannable can be a device or a container - only devices carry a model/serial number,
   * so this is fetched separately and simply left blank when the scannable turns out not to be a device. */
  private loadDeviceExtras(scannableId: number) {
    this.deviceService.getDevice(scannableId).subscribe({
      next: device => {
        this.deviceModel = device.model;
        this.deviceSerialNumber = device.serialNumber;
        this.cdr.markForCheck();
      },
      error: () => {
        this.deviceModel = null;
        this.deviceSerialNumber = null;
        this.cdr.markForCheck();
      }
    });
  }

  public getSelectedScannableId(): number | null {
    return this.selectedDevice?.id ?? null;
  }

  /** Marks every field as touched so Material shows the invalid ones highlighted, as if the user had visited them. */
  public markAllFieldsAsTouched() {
    this.ticketForm.markAllAsTouched();
    this.cdr.markForCheck();
  }
}
