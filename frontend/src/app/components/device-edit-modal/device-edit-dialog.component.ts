import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';
import {OwnerService} from '../../services/owner.service';
import {Owner} from '../../model/owner/owner';
import {MatFormField, MatInput, MatInputModule, MatLabel} from '@angular/material/input';
import {MatCheckbox} from '@angular/material/checkbox';
import {CategoryDetails} from '../../model/category/categoryDetails';
import {CategoryService} from '../../services/category.service';
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from '@angular/material/autocomplete';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {map, Observable, startWith} from 'rxjs';
import {AsyncPipe} from '@angular/common';
import {LocationDetails} from '../../model/location/LocationDetails';
import {LocationService} from '../../services/location.service';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-device-edit-modal',
  imports: [
    MatDialogClose,
    MatButton,
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle,
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
    MatIconModule
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './device-edit-dialog.component.html',
  styleUrl: './device-edit-dialog.component.scss',
})
export class DeviceEditDialogComponent implements OnInit {
  protected owners: Owner[] = [];

  protected categories: CategoryDetails[] = [];
  protected filteredCategories: Observable<CategoryDetails[]>;

  protected locations: LocationDetails[] = [];
  protected filteredLocations: Observable<LocationDetails[]>;

  protected categoryControl = new FormControl();
  protected locationControl = new FormControl();

  constructor(private ownerService: OwnerService,
              private categoryService: CategoryService,
              private locationService: LocationService,) {
    this.filteredCategories = this.categoryControl.valueChanges.pipe(
      startWith(''),
      map(value => this.filterCategories(value || ''))
    );

    this.filteredLocations = this.locationControl.valueChanges.pipe(
      startWith(''),
      map(value => this.filterLocations(value || ''))
    )
  }

  ngOnInit() {
    this.ownerService.getOwners().subscribe(owners => {
      this.owners = owners;
    });

    this.categoryService.getCategories().subscribe(categories => {
      this.categories = categories;
    });

    this.locationService.getLocations().subscribe(locations => {
      this.locations = locations;
    })
  }

  private filterCategories(value: string) {
    const filter = value.toLowerCase();

    return this.categories.filter(category => category.name.toLowerCase().includes(filter)).slice(0, 3);
  }

  private filterLocations(value: string) {
    const filter = value.toLowerCase();

    return this.locations.filter(location => location.name.toLowerCase().includes(filter)).slice(0, 3);
  }

}
