import {
  ChangeDetectionStrategy,
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
import {FormBuilder, ReactiveFormsModule, UntypedFormGroup, Validators} from '@angular/forms';
import {map, Observable, startWith} from 'rxjs';
import {AsyncPipe} from '@angular/common';
import {LocationDetails} from '../../model/location/LocationDetails';
import {LocationService} from '../../services/location.service';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {OwnerDetailsDto} from '../../model/owner/ownerDetailsDto';
import {OwnerService} from '../../services/owner.service';
import {DeviceDetails} from '../../model/scannable/device/deviceDetails';
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

  constructor(
    private fb: FormBuilder,
    private ownerService: OwnerService,
    private categoryService: CategoryService,
    private locationService: LocationService,
  ) {
    this.deviceForm = this.fb.group({
      name: ['', Validators.required],
      isPublicRentable: [false],
      manufacturer: [''],
      model: [''],
      serialNumber: [''],
      category: ['', Validators.required],
      location: ['', Validators.required],
      barcode: ['', Validators.required],
      assetTag: ['', Validators.required],
      weight: ['1'],
      estimatedValue: ['1'],
      quantity: ['1'],
      acquisitionSource: [''],
      acquisitionDate: [new Date()],
      warrantyEndDate: [null],
      owner: [''],
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
  }

  ngOnInit(): void {
    const data = this.deviceData();
    if (data !== null) {
      this.deviceForm.patchValue(data);
    }

    this.deviceForm.valueChanges.subscribe(value => this.formChanged.emit(value));

    this.ownerService.getOwners().subscribe(owners => (this.owners = owners));
    this.categoryService.getCategories().subscribe(categories => (this.categories = categories));
    this.locationService.getLocations().subscribe(locations => (this.locations = locations));
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

  protected displayOwner(owner: OwnerDetailsDto | string | null): string {
    if (!owner) return '';
    if (typeof owner === 'string') return owner;
    return owner.name;
  }

}
