import {
  ChangeDetectionStrategy,
  Component,
  effect,
  input,
  OnInit,
  output,
  ViewChild,
  inject,
} from '@angular/core';
import {
  MatFormField,
  MatInput,
  MatInputModule,
  MatLabel,
  MatSuffix,
} from '@angular/material/input';
import { MatCheckbox } from '@angular/material/checkbox';
import { CategoryDetails } from '../../model/category/categoryDetails';
import { CategoryService } from '../../services/category.service';
import { MatAutocomplete, MatAutocompleteTrigger, MatOption } from '@angular/material/autocomplete';
import {
  AbstractControl,
  AsyncValidatorFn,
  FormBuilder,
  FormControl,
  ReactiveFormsModule,
  UntypedFormGroup,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { catchError, map, Observable, of, startWith, switchMap, timer } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { LocationDetails } from '../../model/location/LocationDetails';
import { LocationService } from '../../services/location.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { OwnerDetailsDto } from '../../model/owner/ownerDetailsDto';
import { OwnerService } from '../../services/owner.service';
import { ContainerDetails } from '../../model/scannable/container/containerDetails';
import { DeviceDetails } from '../../model/scannable/device/deviceDetails';
import { DeviceService } from '../../services/device.service';
import { findByBarcode } from '../../util/ean8';
import { ScannableService } from '../../services/scannable.service';
import { MatIconButton } from '@angular/material/button';
import {
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle,
} from '@angular/material/expansion';
import { MatDialog } from '@angular/material/dialog';
import {
  QuantityInputDialogData,
  QuantityInputModalComponent,
} from '../quantity-input-modal/quantity-input-modal.component';
import { YesnoModalComponent } from '../yesno-modal/yesno-modal.component';
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
import { environment } from '../../../environments/environment';
import { incrementTrailingNumber } from '../../util/incrementTrailingNumber';

export interface AddDeviceEvent {
  device: DeviceDetails;
  quantity: number;
}

@Component({
  selector: 'app-container-form',
  imports: [
    MatFormField,
    MatLabel,
    MatInput,
    MatCheckbox,
    MatAutocomplete,
    MatOption,
    ReactiveFormsModule,
    MatAutocompleteTrigger,
    AsyncPipe,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatIcon,
    MatIconButton,
    MatSuffix,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
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
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './container-form.component.html',
  styleUrl: './container-form.component.scss',
})
export class ContainerFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private ownerService = inject(OwnerService);
  private categoryService = inject(CategoryService);
  private locationService = inject(LocationService);
  private deviceService = inject(DeviceService);
  private scannableService = inject(ScannableService);
  private dialog = inject(MatDialog);

  @ViewChild(MatTable) itemsTable?: MatTable<unknown>;

  /** Pass an existing container to pre-populate the form, or leave undefined for a blank create form. */
  containerData = input<ContainerDetails | null>(null);

  /** Pass another container to pre-fill a new (blank) form with its values, except the
   * barcode, which is still freshly auto-generated. */
  duplicateFrom = input<ContainerDetails | null>(null);

  /** Emits the latest raw form value whenever the user makes a change. */
  formChanged = output<Partial<ContainerDetails>>();

  /** Emits the resolved device (and chosen quantity) to add once a barcode is matched or an autocomplete option is picked. */
  addDevice = output<AddDeviceEvent>();
  /** Emitted when the entered value doesn't match any known device's barcode. */
  deviceNotFound = output<void>();
  /** Emits the device to remove from the container. */
  removeDevice = output<DeviceDetails>();

  containerForm: UntypedFormGroup;

  protected categories: CategoryDetails[] = [];
  protected filteredCategories: Observable<CategoryDetails[]>;

  protected locations: LocationDetails[] = [];
  protected filteredLocations: Observable<LocationDetails[]>;

  protected owners: OwnerDetailsDto[] = [];
  protected filteredOwners: Observable<OwnerDetailsDto[]>;

  protected addDeviceFormControl = new FormControl();
  protected devices: DeviceDetails[] = [];
  protected filteredNewDeviceOptions: Observable<DeviceDetails[]>;

  protected itemColumns = ['name', 'model', 'assetTag', 'remove'];

  constructor() {
    this.containerForm = this.fb.group({
      name: ['', Validators.required],
      publicRentable: [false],
      category: ['', Validators.required],
      location: ['', Validators.required],
      barcode: ['', Validators.required, this.barcodeTakenValidator()],
      assetTag: ['', Validators.required, this.assetTagTakenValidator()],
      weight: ['1'],
      owner: ['', Validators.required],
    });

    this.filteredCategories = this.containerForm.get('category')!.valueChanges.pipe(
      startWith(''),
      map((value) => this.filterCategories(value || '')),
    );

    this.filteredLocations = this.containerForm.get('location')!.valueChanges.pipe(
      startWith(''),
      map((value) => this.filterLocations(value || '')),
    );

    this.filteredOwners = this.containerForm.get('owner')!.valueChanges.pipe(
      startWith(''),
      map((value) => this.filterOwners(value || '')),
    );

    this.filteredNewDeviceOptions = this.addDeviceFormControl.valueChanges.pipe(
      startWith(''),
      map((value) => this.filterDevices(value || '')),
    );

    // The items table binds [dataSource] to containerData().items, which is a plain array -
    // when the host dialog swaps in a fresh container after add/remove, CdkTable doesn't
    // always notice on its own, so force it to redraw whenever the input actually changes.
    effect(() => {
      this.containerData();
      this.itemsTable?.renderRows();
    });
  }

  ngOnInit(): void {
    const data = this.containerData();
    const duplicateFrom = this.duplicateFrom();
    if (data !== null) {
      this.containerForm.patchValue(data);
    } else if (duplicateFrom !== null) {
      this.containerForm.patchValue(duplicateFrom);
      this.containerForm.get('assetTag')!.setValue(incrementTrailingNumber(duplicateFrom.assetTag));
      this.generateBarcode();
    } else {
      this.generateBarcode();
    }

    this.containerForm.valueChanges.subscribe((value) => this.formChanged.emit(value));

    // Each filtered stream below is seeded via startWith('') at construction time, before
    // this data has loaded - re-running validity once it arrives forces a fresh filter pass
    // instead of leaving the panel showing the empty result cached from that initial seed.
    this.ownerService.getOwners().subscribe((owners) => {
      this.owners = owners;

      if (data === null && duplicateFrom === null) {
        const defaultOwner = owners.find((owner) => owner.name === environment.defaultOwnerName);
        if (defaultOwner) {
          this.containerForm.get('owner')!.setValue(defaultOwner);
          return;
        }
      }

      this.containerForm.get('owner')!.updateValueAndValidity();
    });
    this.categoryService.getCategories().subscribe((categories) => {
      this.categories = categories;
      this.containerForm.get('category')!.updateValueAndValidity();
    });
    this.locationService.getLocations().subscribe((locations) => {
      this.locations = locations;
      this.containerForm.get('location')!.updateValueAndValidity();
    });
    this.deviceService.getDevices().subscribe((devices) => {
      this.devices = devices;
      this.addDeviceFormControl.updateValueAndValidity();
    });
  }

  private generateBarcode() {
    this.scannableService.getScannablesCount().subscribe((count) => {
      this.findAvailableBarcode(count + 1);
    });
  }

  private findAvailableBarcode(candidate: number) {
    const candidateBarcode = candidate.toString().padStart(7, '0');

    this.scannableService.isBarcodeTaken(candidateBarcode).subscribe((taken) => {
      if (taken) {
        this.findAvailableBarcode(candidate + 1);
      } else {
        this.containerForm.get('barcode')!.setValue(candidateBarcode);
      }
    });
  }

  private assetTagTakenValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      const value = control.value;
      if (!value || value === this.containerData()?.assetTag) {
        return of(null);
      }

      return timer(400).pipe(
        switchMap(() => this.scannableService.isAssetTagTaken(value)),
        map((taken) => (taken ? { assetTagTaken: true } : null)),
        catchError(() => of(null)),
      );
    };
  }

  private barcodeTakenValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      const value = control.value;
      if (!value || value === this.containerData()?.barcode) {
        return of(null);
      }

      return timer(400).pipe(
        switchMap(() => this.scannableService.isBarcodeTaken(value)),
        map((taken) => (taken ? { barcodeTaken: true } : null)),
        catchError(() => of(null)),
      );
    };
  }

  private filterCategories(value: string): CategoryDetails[] {
    const filter = value.toLowerCase();
    return this.categories.filter((c) => c.name.toLowerCase().includes(filter)).slice(0, 3);
  }

  private filterLocations(value: string): LocationDetails[] {
    const filter = value.toLowerCase();
    return this.locations.filter((l) => l.name.toLowerCase().includes(filter)).slice(0, 3);
  }

  private filterOwners(value: OwnerDetailsDto | string): OwnerDetailsDto[] {
    const filter = (typeof value === 'string' ? value : value.name).toLowerCase();
    return this.owners.filter((o) => o.name.toLowerCase().includes(filter)).slice(0, 3);
  }

  private filterDevices(value: string): DeviceDetails[] {
    const filter = value.toLowerCase();
    if (filter.length < 2) {
      return [];
    }

    const addedDeviceIds = new Set(
      (this.containerData()?.items ?? []).map((item) => item.device.id),
    );

    return this.devices
      .filter(
        (device) =>
          !addedDeviceIds.has(device.id) &&
          (device.name.toLowerCase().includes(filter) ||
            (device.model ?? '').toLowerCase().includes(filter) ||
            (device.manufacturer ?? '').toLowerCase().includes(filter)),
      )
      .slice(0, 5);
  }

  protected displayOwner(owner: OwnerDetailsDto | string | null): string {
    if (!owner) return '';
    if (typeof owner === 'string') return owner;
    return owner.name;
  }

  // MatAutocomplete's Enter-to-select handling runs on keydown and fires (optionSelected),
  // but the same keypress still produces a native keyup afterward that also hits our own
  // (keyup.enter) handler - this flag stops that from adding the device a second time.
  private justSelectedDeviceFromDropdown = false;

  protected onDeviceOptionSelected() {
    this.justSelectedDeviceFromDropdown = true;
    this.addDeviceToContainer();
  }

  protected onAddDeviceEnterKey() {
    if (this.justSelectedDeviceFromDropdown) {
      this.justSelectedDeviceFromDropdown = false;
      return;
    }

    this.addDeviceToContainer();
  }

  protected addDeviceToContainer() {
    const enteredValue: string = this.addDeviceFormControl.value ?? '';
    if (!enteredValue) {
      return;
    }

    const matchedDevice = findByBarcode(this.devices, (device) => device.barcode, enteredValue);
    if (!matchedDevice) {
      this.deviceNotFound.emit();
      return;
    }

    if (matchedDevice.quantity > 1) {
      const quantityDialog = this.dialog.open(QuantityInputModalComponent, {
        width: '20vw',
        minWidth: '350px',
        data: new QuantityInputDialogData(matchedDevice.name, matchedDevice.quantity),
      });

      quantityDialog.afterClosed().subscribe((chosenQuantity) => {
        if (chosenQuantity) {
          this.addDevice.emit({ device: matchedDevice, quantity: chosenQuantity });
          this.addDeviceFormControl.reset();
        }
      });
    } else {
      this.addDevice.emit({ device: matchedDevice, quantity: 1 });
      this.addDeviceFormControl.reset();
    }
  }

  protected removeDeviceFromContainer(device: DeviceDetails) {
    const confirmDialog = this.dialog.open(YesnoModalComponent, {
      width: '20vw',
      minWidth: '350px',
      data: `Biztos eltávolítod a(z) ${device.name} eszközt a szállítóládából?`,
    });

    confirmDialog.afterClosed().subscribe((result) => {
      if (result) {
        this.removeDevice.emit(device);
      }
    });
  }
}
