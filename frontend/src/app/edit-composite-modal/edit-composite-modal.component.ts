import {Component, Input, OnInit} from '@angular/core';
import {CompositeItem} from '../_model/CompositeItem';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {Location} from '../_model/Location';
import {CompositeService} from '../_services/composite.service';
import {LocationService} from '../_services/location.service';
import {NgbActiveModal, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Device} from '../_model/Device';
import * as $ from 'jquery';
import {DeviceService} from '../_services/device.service';
import {ScannableService} from '../_services/scannable.service';
import {UserService} from '../_services/user.service';
import {User} from '../_model/User';
import {BarcodePurifier} from '../_services/barcode-purifier.service';
import {switchMap, tap} from 'rxjs/operators';
import {barcodeValidator} from '../helpers/barcode.validator';
import {textIdValidator} from '../helpers/textId.validator';
import {Category} from '../_model/Category';
import {CategoryService} from '../_services/category.service';
import {Scannable} from '../_model/Scannable';
import {TicketStatus} from '../_model/TicketStatus';
import {RentItemWithRentData} from '../_model/RentItemWithRentData';
import {Ticket} from '../_model/Ticket';
import {Router} from '@angular/router';
import {EditTicketComponent} from '../edit-ticket/edit-ticket.component';

@Component({
    selector: 'app-edit-composite-modal',
    templateUrl: './edit-composite-modal.component.html',
    styleUrls: ['./edit-composite-modal.component.css']
})
export class EditCompositeModalComponent implements OnInit {
    @Input() title: string;
    @Input() compositeItem: CompositeItem

    compositeDataForm: UntypedFormGroup;

    categoryOptions: Category[];
    filteredCategoryOptions: Category[];
    locationOptions: Location[];
    filteredLocationOptions: Location[];
    addDeviceFormControl = new UntypedFormControl();
    fullAccessMember = false;
    deleteConfirmed = false;

    private currentLocationInput = '';
    private currentCategoryInput = '';

    allDevices: Device[] = [];
    filteredNewDeviceOptions: Scannable[] = [];

    rentitemsAndRents: RentItemWithRentData[] = [];
    tickets: Ticket[] = [];

    constructor(public activeModal: NgbActiveModal,
                private fb: UntypedFormBuilder,
                private compositeItemService: CompositeService,
                private locationService: LocationService,
                private categoryService: CategoryService,
                private deviceService: DeviceService,
                private scannableService: ScannableService,
                private userService: UserService,
                private router: Router,
                private modalService: NgbModal) {
        if (this.compositeItem === undefined) {
            this.compositeItem = new CompositeItem();
        }

        this.userService.getCurrentUser().subscribe(user => {
            this.fullAccessMember = user.isFullAccessMember();
        });

        this.deviceService.getDevices().subscribe(devices => {
            this.allDevices = devices;
        })
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

            this.scannableService.getRentsOfScannable(this.compositeItem.id).subscribe(result => {
                this.rentitemsAndRents = result
                    .filter(item => !item.rent.isClosed)
                    .sort((a, b) => b.rent.expBackDate.getTime() - a.rent.expBackDate.getTime());

                result
                    .filter(item => item.rent.isClosed)
                    .sort((a, b) => b.rent.expBackDate.getTime() - a.rent.expBackDate.getTime())
                    .forEach(item => this.rentitemsAndRents.push(item));
            });

            this.scannableService.getTicketsOfScannable(this.compositeItem.id).subscribe(result => {
                this.tickets = result.filter(item => !(item.status === TicketStatus.CLOSED));

                this.tickets.sort((a, b) => b.dateOfWriting.getTime() - a.dateOfWriting.getTime());

                result
                    .filter(item => (item.status === TicketStatus.CLOSED))
                    .sort((a, b) => b.dateOfWriting.getTime() - a.dateOfWriting.getTime())
                    .forEach(item => this.tickets.push(item));
            });
        }

        this.compositeDataForm.get('barcode').markAsTouched();
        this.compositeDataForm.get('textIdentifier').markAsTouched();

        this.addDeviceFormControl.valueChanges.subscribe(value => {
            if (value === '' || value === null) {
                this.filteredNewDeviceOptions = []
                return;
            }

            let filteredItems = [];

            const items = this.allDevices.sort((a, b) => a.name.localeCompare(b.name));
            filteredItems = items.filter(item => item.name.toLowerCase().includes(value.toLowerCase()))
                .filter(item => this.compositeItem.devices.find(i => i.id === item.id) === undefined)

            if (filteredItems.length < 10) {
                this.filteredNewDeviceOptions = filteredItems;
            } else {
                this.filteredNewDeviceOptions = [];
            }
        });
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

    goToRent(rentId: number) {
        this.activeModal.dismiss('noRedirect');

        this.router.navigateByUrl('/rent/' + rentId);
    }

    editTicket(id: number) {
        this.activeModal.dismiss('noRedirect');

        this.router.navigateByUrl('/tickets/' + id);
    }

    createTicket() {
        const ticketModal = this.modalService.open(EditTicketComponent, {size: 'lg'});
        ticketModal.componentInstance.title = 'Új hibajegy';
        ticketModal.componentInstance.ticket = new Ticket();
        ticketModal.componentInstance.scannable = this.compositeItem;

        ticketModal.result.catch(reason => {
            if (reason === 'save') {
                this.tickets.push(ticketModal.componentInstance.ticket);
            }
        })
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
