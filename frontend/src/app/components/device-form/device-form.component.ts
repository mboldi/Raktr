import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  input,
  OnInit,
  output,
} from '@angular/core';
import {MatFormField, MatInput, MatInputModule, MatLabel} from '@angular/material/input';
import {MatCheckbox} from '@angular/material/checkbox';
import {CategoryDetails} from '../../model/category/categoryDetails';
import {CategoryService} from '../../services/category.service';
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from '@angular/material/autocomplete';
import {
  AbstractControl,
  AsyncValidatorFn,
  FormBuilder,
  ReactiveFormsModule,
  UntypedFormGroup,
  ValidationErrors,
  Validators
} from '@angular/forms';
import {catchError, map, Observable, of, startWith, switchMap, timer} from 'rxjs';
import {ScannableService} from '../../services/scannable.service';
import {AsyncPipe} from '@angular/common';
import {LocationDetails} from '../../model/location/LocationDetails';
import {LocationService} from '../../services/location.service';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {OwnerDetailsDto} from '../../model/owner/ownerDetailsDto';
import {OwnerService} from '../../services/owner.service';
import {DeviceDetails} from '../../model/scannable/device/deviceDetails';
import {DeviceService} from '../../services/device.service';
import {MatSelect} from '@angular/material/select';
import {DeviceStatus} from '../../model/scannable/device/deviceStatus';

@Component({
  selector: 'app-device-form',
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
    MatDatepickerModule,
    MatIconModule,
    MatSelect,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './device-form.component.html',
  styleUrl: './device-form.component.scss',
})
export class DeviceFormComponent implements OnInit {
  /** Pass an existing device to pre-populate the form, or leave undefined for a blank create form. */
  deviceData = input<DeviceDetails | null>(null);

  /** Emits the latest raw form value whenever the user makes a change. */
  formChanged = output<Partial<DeviceDetails>>();

  /** Expose the form group so the host (dialog, page, …) can read validity / values. */
  deviceForm: UntypedFormGroup;

  protected readonly DeviceStatus = DeviceStatus;

  protected categories: CategoryDetails[] = [];
  protected filteredCategories: Observable<CategoryDetails[]>;

  protected locations: LocationDetails[] = [];
  protected filteredLocations: Observable<LocationDetails[]>;

  protected owners: OwnerDetailsDto[] = [];
  protected filteredOwners: Observable<OwnerDetailsDto[]>;

  protected manufacturers: string[] = [];
  protected filteredManufacturers: Observable<string[]>;

  constructor(
    private fb: FormBuilder,
    private ownerService: OwnerService,
    private categoryService: CategoryService,
    private locationService: LocationService,
    private scannableService: ScannableService,
    private deviceService: DeviceService,
    private cdr: ChangeDetectorRef,
  ) {
    this.deviceForm = this.fb.group({
      name: ['', Validators.required],
      isPublicRentable: [false],
      manufacturer: [''],
      model: [''],
      serialNumber: [''],
      category: ['', Validators.required],
      location: ['', Validators.required],
      barcode: ['', Validators.required, this.barcodeTakenValidator()],
      assetTag: ['', Validators.required, this.assetTagTakenValidator()],
      weight: ['1'],
      estimatedValue: ['1'],
      quantity: ['1'],
      acquisitionSource: [''],
      acquisitionDate: [new Date()],
      warrantyEndDate: [null],
      owner: ['', Validators.required],
      notes: [''],
      status: [DeviceStatus.GOOD],
    });

    this.filteredCategories = this.deviceForm.get('category')!.valueChanges.pipe(
      startWith(''),
      map(value => this.filterCategories(value || ''))
    );

    this.filteredLocations = this.deviceForm.get('location')!.valueChanges.pipe(
      startWith(''),
      map(value => this.filterLocations(value || ''))
    );

    this.filteredOwners = this.deviceForm.get('owner')!.valueChanges.pipe(
      startWith(''),
      map(value => this.filterOwners(value || ''))
    );

    this.filteredManufacturers = this.deviceForm.get('manufacturer')!.valueChanges.pipe(
      startWith(''),
      map(value => this.filterManufacturers(value || ''))
    );
  }

  ngOnInit(): void {
    const data = this.deviceData();
    if (data !== null) {
      this.deviceForm.patchValue(data);
    } else {
      this.generateBarcode();
    }

    this.deviceForm.valueChanges.subscribe(value => this.formChanged.emit(value));

    this.ownerService.getOwners().subscribe(owners => {
      this.owners = owners;

      if (data === null) {
        const defaultOwner = owners.find(owner => owner.name === 'SVIE');
        if (defaultOwner) {
          this.deviceForm.get('owner')!.setValue(defaultOwner);
          return;
        }
      }

      // Re-run the filter now that the owner list has actually loaded - the initial
      // startWith('') seed ran before this data arrived, so it cached an empty result.
      this.deviceForm.get('owner')!.updateValueAndValidity();
    });
    this.categoryService.getCategories().subscribe(categories => {
      this.categories = categories;
      this.deviceForm.get('category')!.updateValueAndValidity();
    });
    this.locationService.getLocations().subscribe(locations => {
      this.locations = locations;
      this.deviceForm.get('location')!.updateValueAndValidity();
    });
    this.deviceService.getManufacturers().subscribe(manufacturers => {
      this.manufacturers = manufacturers;
      this.deviceForm.get('manufacturer')!.updateValueAndValidity();
    });
  }

  private generateBarcode() {
    this.scannableService.getScannablesCount().subscribe(count => {
      this.findAvailableBarcode(count + 1);
    });
  }

  private findAvailableBarcode(candidate: number) {
    const candidateBarcode = candidate.toString().padStart(7, '0');

    this.scannableService.isBarcodeTaken(candidateBarcode).subscribe(taken => {
      if (taken) {
        this.findAvailableBarcode(candidate + 1);
      } else {
        this.deviceForm.get('barcode')!.setValue(candidateBarcode);
      }
    });
  }

  private assetTagTakenValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      const value = control.value;
      if (!value || value === this.deviceData()?.assetTag) {
        return of(null);
      }

      return timer(400).pipe(

        switchMap(() => this.scannableService.isAssetTagTaken(value)),
        map(taken => (taken ? {assetTagTaken: true} : null)),
        catchError(() => of(null))
      );
    };
  }

  private barcodeTakenValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      const value = control.value;
      if (!value || value === this.deviceData()?.barcode) {
        return of(null);
      }

      return timer(400).pipe(
        switchMap(() => this.scannableService.isBarcodeTaken(value)),
        map(taken => (taken ? {barcodeTaken: true} : null)),
        catchError(() => of(null))
      );
    };
  }

  private filterCategories(value: string): CategoryDetails[] {
    const filter = value.toLowerCase();
    return this.categories.filter(c => c.name.toLowerCase().includes(filter)).slice(0, 3);
  }

  private filterLocations(value: string): LocationDetails[] {
    const filter = value.toLowerCase();
    return this.locations.filter(l => l.name.toLowerCase().includes(filter)).slice(0, 3);
  }

  private filterOwners(value: OwnerDetailsDto | string): OwnerDetailsDto[] {
    const filter = (typeof value === 'string' ? value : value.name).toLowerCase();
    return this.owners.filter(o => o.name.toLowerCase().includes(filter)).slice(0, 3);
  }

  private filterManufacturers(value: string): string[] {
    const filter = value.toLowerCase();
    return this.manufacturers.filter(m => m.toLowerCase().includes(filter)).slice(0, 3);
  }

  /** Marks every field as touched so Material shows the invalid ones highlighted, as if the user had visited them. */
  public markAllFieldsAsTouched() {
    this.deviceForm.markAllAsTouched();
    this.cdr.markForCheck();
  }

  protected displayOwner(owner: OwnerDetailsDto | string | null): string {
    if (!owner) return '';
    if (typeof owner === 'string') return owner;
    return owner.name;
  }

}
