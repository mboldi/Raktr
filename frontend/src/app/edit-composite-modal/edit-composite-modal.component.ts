import {Component, Input, OnInit} from '@angular/core';
import {CompositeItem} from '../model/CompositeItem';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {Location} from '../model/Location';
import {CompositeService} from '../_services/composite.service';
import {LocationService} from '../_services/location.service';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {Device} from '../model/Device';
import * as $ from 'jquery';
import {DeviceService} from '../_services/device.service';
import {ScannableService} from '../_services/scannable.service';
import {UserService} from '../_services/user.service';
import {User} from '../model/User';
import {BarcodePurifier} from '../_services/barcode-purifier.service';
import {switchMap, tap} from 'rxjs/operators';
import {barcodeValidator} from '../helpers/barcode.validator';
import {textIdValidator} from '../helpers/textId.validator';
import {Category} from '../model/Category';
import {CategoryService} from '../_services/category.service';

@Component({
    selector: 'app-edit-composite-modal',
    templateUrl: './edit-composite-modal.component.html',
    styleUrls: ['./edit-composite-modal.component.css']
})
export class EditCompositeModalComponent implements OnInit {
    @Input() title: string;
    @Input() compositeItem: CompositeItem

    compositeDataForm: FormGroup;

    categoryOptions: Category[];
    filteredCategoryOptions: Category[];
    locationOptions: Location[];
    filteredLocationOptions: Location[];
    addDeviceFormControl = new FormControl();
    fullAccessMember = false;
    deleteConfirmed = false;

    private currentLocationInput = '';
    private currentCategoryInput = '';

    constructor(public activeModal: NgbActiveModal,
                private fb: FormBuilder,
                private compositeItemService: CompositeService,
                private locationService: LocationService,
                private categoryService: CategoryService,
                private deviceService: DeviceService,
                private scannableService: ScannableService,
                private userService: UserService) {
        if (this.compositeItem === undefined) {
            this.compositeItem = new CompositeItem();
        }

        this.userService.getCurrentUser().subscribe(user => {
            this.fullAccessMember = User.isFullAccessMember(user);
        });
    }

    ngOnInit(): void {
        this.compositeDataForm = this.fb.group({
            name: ['', Validators.required],
            isPublicRentable: [''],
            location: ['', Validators.required],
            category: ['', Validators.required],
            barcode: ['', Validators.required, barcodeValidator(this.scannableService, this.compositeItem.id)],
            textIdentifier: ['', Validators.required, textIdValidator(this.scannableService, this.compositeItem.id)]
        });

        this.compositeDataForm
            .get('category')
            .valueChanges
            .pipe(
                tap(value => this.currentCategoryInput = value),
                switchMap(value => this.categoryService.getCategories())
            )
            .subscribe(categories => {
                this.categoryOptions = categories;
                this.filteredCategoryOptions = this._filterCategories(categories, this.currentCategoryInput);
            });

        this.compositeDataForm
            .get('location')
            .valueChanges
            .pipe(
                tap(value => this.currentLocationInput = value),
                switchMap(value => this.locationService.getLocations())
            )
            .subscribe(locations => {
                this.locationOptions = locations;
                this.filteredLocationOptions = this._filterLocations(locations, this.currentLocationInput);
            });

        if (this.compositeItem.id === null || this.compositeItem.id === -1) {
            this.scannableService.getNextId().subscribe(
                nextid => {
                    // @ts-ignore
                    this.compositeItem.barcode = nextid.toString().padStart(7, '0');

                    this.setFormData();
                });
        } else {
            this.setFormData();
        }

        this.compositeDataForm.get('barcode').markAsTouched();
        this.compositeDataForm.get('textIdentifier').markAsTouched();
    }

    private setFormData() {
        this.compositeDataForm.setValue({
            name: this.compositeItem.name,
            isPublicRentable: this.compositeItem.isPublicRentable,
            category: this.compositeItem.category === null ? '' : this.compositeItem.category.name,
            location: this.compositeItem.location === null ? '' : this.compositeItem.location.name,
            barcode: this.compositeItem.barcode,
            textIdentifier: this.compositeItem.textIdentifier,
        });
    }

    private _filterCategories(categories: Category[], value: string): Category[] {
        const filterValue = value.toLowerCase();

        return categories.filter(category => category.name.toLowerCase().includes(filterValue));
    }

    private _filterLocations(locations: Location[], value: string): Location[] {
        const filterValue = value.toLowerCase();

        return locations.filter(location => location.name.toLowerCase().includes(filterValue));
    }

    save() {
        const value = this.compositeDataForm.value;
        this.compositeItem.name = value.name.toString();
        this.compositeItem.isPublicRentable = value.isPublicRentable;
        this.compositeItem.barcode = value.barcode.toString();
        this.compositeItem.textIdentifier = value.textIdentifier.toString();
        this.compositeItem.location = new Location(-1, value.location.toString());
        this.compositeItem.category = new Category(-1, value.category.toString());

        if (this.compositeItem.id === -1) {
            // new
            this.compositeItemService.addCompositeItem(this.compositeItem).subscribe(
                compositeItem => {
                    this.compositeItem = compositeItem;
                    this.showNotification('Mentve', 'success');
                },
                (error) => {
                    this.compositeItem.id = -1;
                    this.showNotification('Hiba!', 'warning');
                });
        } else {
            this.compositeItemService.updateCompositeItem(this.compositeItem).subscribe(
                compositeItem => {
                    this.compositeItem = compositeItem;
                    this.compositeDataForm.setValue({
                        name: compositeItem.name,
                        isPublicRentable: compositeItem.isPublicRentable,
                        location: compositeItem.location.name,
                        barcode: compositeItem.barcode,
                        textIdentifier: compositeItem.textIdentifier
                    });

                    this.showNotification('Mentve', 'success');
                },
                error => {
                    this.showNotification('Hiba!', 'warning');
                });
        }
    }

    removeFromComposite(device: Device) {
        if (this.inComposite(device)) {
            this.compositeItemService.removeDeviceFromComposite(device, this.compositeItem.id).subscribe(composite => {
                this.compositeItem = composite;
                this.compositeItem.devices.push();
                this.showNotification(`${device.name} eltávolítva`, 'success');
            })
        } else {
            this.showNotification('Ez az eszköz nem eleme az összetett eszköznek', 'warning');
        }
    }

    addDeviceToComposite() {
        const barcode = BarcodePurifier.purify(this.addDeviceFormControl.value);

        this.scannableService.getScannableByBarcode(barcode).subscribe(scannable => {
                if (scannable === undefined) {
                    this.showNotification('Nem találtam ilyen eszközt', 'warning');
                } else if (scannable['type_'] === 'compositeItem') {
                    this.showNotification('Összetett eszközt nem lehet hozzáadni!', 'warning');
                } else if (this.inComposite(scannable as Device)) {
                    this.showNotification('Ezt az eszközt (' + scannable.name + ') már tartalmazza az összetett eszköz!', 'warning');
                } else {
                    this.compositeItem.devices.push(scannable as Device);
                    this.compositeItemService.addDeviceToComposite(scannable as Device, this.compositeItem.id)
                        .subscribe(composite => {
                            this.compositeItem = composite;
                            this.showNotification(`${scannable.name} hozzáadva!`, 'success');
                        });

                    this.addDeviceFormControl.setValue('');
                }
            },
            error => this.showNotification('Nem tudtam hozzáadni!', 'warning'));
    }

    inComposite(device: Device): boolean {
        let present = false;
        this.compositeItem.devices.forEach(device_ => {
            if (device_.barcode === device.barcode) {
                present = true;
            }
        })
        return present;
    }

    delete(compositeItem: CompositeItem) {
        this.compositeItemService.deleteComposite(compositeItem).subscribe(
            compositeItem_ => this.activeModal.dismiss('delete')
        )
    }

    showNotification(message_: string, type: string) {
        $['notify']({
            icon: 'add_alert',
            message: message_
        }, {
            type: type,
            timer: 1000,
            placement: {
                from: 'top',
                align: 'right'
            },
            z_index: 2000
        })
    }
}
