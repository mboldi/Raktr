import {Component, Input, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Category} from '../_model/Category';
import {Location} from '../_model/Location';
import {switchMap, tap} from 'rxjs/operators';
import {NgbActiveModal, NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Device} from '../_model/Device';
import {LocationService} from '../_services/location.service';
import {CategoryService} from '../_services/category.service';
import {DeviceService} from '../_services/device.service';
import {UserService} from '../_services/user.service';
import {MatDialog} from '@angular/material/dialog';
import * as $ from 'jquery';
import {ScannableService} from '../_services/scannable.service';
import {barcodeValidator} from '../helpers/barcode.validator';
import {textIdValidator} from '../helpers/textId.validator';
import {DeviceStatus} from '../_model/DeviceStatus';
import {Owner} from '../_model/Owner';
import {OwnerService} from '../_services/owner.service';
import {RentItemWithRentData} from '../_model/RentItemWithRentData';
import {Ticket} from '../_model/Ticket';
import {Router} from '@angular/router';
import {TicketStatus} from '../_model/TicketStatus';
import {EditTicketComponent} from '../edit-ticket/edit-ticket.component';
import {tick} from '@angular/core/testing';
import {GeneralDataService} from '../_services/general-data.service';

@Component({
    selector: 'app-edit-device-modal',
    templateUrl: './edit-device-modal.component.html',
    styleUrls: ['./edit-device-modal.component.css']
})
export class EditDeviceModalComponent implements OnInit {
    @Input() title: string;
    @Input() device: Device;

    categoryOptions: Category[];
    filteredCategoryOptions: Category[];
    locationOptions: Location[];
    filteredLocationOptions: Location[];
    ownerOptions: Owner[];
    filteredOwnerOptions: Owner[];
    deviceForm: UntypedFormGroup;
    admin = false;
    deleteConfirmed = false;

    forceEan8 = false;

    warrantyActive: boolean;

    currentCategoryInput = '';
    currentLocationInput = '';
    currentOwnerInput = '';

    rentitemsAndRents: RentItemWithRentData[] = [];
    tickets: Ticket[] = [];

    constructor(public activeModal: NgbActiveModal,
                private fb: UntypedFormBuilder,
                private locationService: LocationService,
                private categoryService: CategoryService,
                private ownerService: OwnerService,
                private deviceService: DeviceService,
                private scannableService: ScannableService,
                private userService: UserService,
                private generalDataService: GeneralDataService,
                public dialog: MatDialog,
                private router: Router,
                private modalService: NgbModal) {
        if (this.device === undefined) {
            this.device = new Device();
            this.device.id = -1;
        }
    }

    ngOnInit(): void {
        this.deviceForm = this.fb.group({
            name: ['', Validators.required],
            isPublicRentable: [''],
            maker: [''],
            type: [''],
            serial: [''],
            category: ['', Validators.required],
            location: ['', Validators.required],
            barcode: ['', Validators.required, barcodeValidator(this.scannableService, this.device.id)],
            textIdentifier: ['', Validators.required, textIdValidator(this.scannableService, this.device.id)],
            weight: ['1'],
            value: ['1'],
            quantity: ['1'],
            acquiredFrom: [''],
            dateOfAcquisition: [new Date()],
            endOfWarranty: [null],
            owner: [''],
            comment: ['']
        });

        if (this.device.id === null || this.device.id === -1) {
            this.scannableService.getNextId().subscribe(nextid => {
                // @ts-ignore
                this.device.barcode = nextid.toString().padStart(7, '0');

                this.setFormFields();
            })
        } else {
            this.setFormFields();

            this.warrantyActive = this.device.endOfWarranty >= new Date();

            this.scannableService.getRentsOfScannable(this.device.id).subscribe(result => {
                this.rentitemsAndRents = result
                    .filter(item => !item.rent.isClosed)
                    .sort((a, b) => b.rent.expBackDate.getTime() - a.rent.expBackDate.getTime());

                result
                    .filter(item => item.rent.isClosed)
                    .sort((a, b) => b.rent.expBackDate.getTime() - a.rent.expBackDate.getTime())
                    .forEach(item => this.rentitemsAndRents.push(item));
            });

            this.scannableService.getTicketsOfScannable(this.device.id).subscribe(result => {
                this.tickets = result.filter(item => !(item.status === TicketStatus.CLOSED));

                this.tickets.sort((a, b) => b.dateOfWriting.getTime() - a.dateOfWriting.getTime());

                result
                    .filter(item => (item.status === TicketStatus.CLOSED))
                    .sort((a, b) => b.dateOfWriting.getTime() - a.dateOfWriting.getTime())
                    .forEach(item => this.tickets.push(item));
            });
        }

        this.userService.getCurrentUser().subscribe(user => {
            this.admin = user.isFullAccessMember();
        });

        this.deviceForm
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

        this.deviceForm
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

        this.deviceForm
            .get('owner')
            .valueChanges
            .pipe(
                tap(value => this.currentOwnerInput = value),
                switchMap(value => this.ownerService.getOwners())
            )
            .subscribe(owners => {
                this.ownerOptions = owners;
                this.filteredOwnerOptions = this._filterOwners(owners, this.currentOwnerInput);
            });

        this.deviceForm.get('barcode').markAsTouched();
        this.deviceForm.get('textIdentifier').markAsTouched();
        this.deviceForm.get('category').markAsTouched();
        this.deviceForm.get('location').markAsTouched();

        this.generalDataService.getByKey('forceEan8Barcode').subscribe(result => {
            this.forceEan8 = result === undefined ? false : result.data.toLowerCase() === 'true';
        });
    }

    private setFormFields() {
        this.deviceForm.setValue({
            name: this.device.name,
            isPublicRentable: this.device.isPublicRentable,
            maker: this.device.maker,
            type: this.device.type,
            serial: this.device.serial,
            category: this.device.category === null ? '' : this.device.category.name,
            location: this.device.location === null ? '' : this.device.location.name,
            barcode: this.device.barcode,
            textIdentifier: this.device.textIdentifier,
            weight: this.device.weight,
            value: this.device.value,
            quantity: this.device.quantity,
            acquiredFrom: this.device.aquiredFrom,
            dateOfAcquisition: this.device.dateOfAcquisition,
            endOfWarranty: this.device.endOfWarranty,
            owner: this.device.owner === null ? '' : this.device.owner.name,
            comment: this.device.comment
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

    private _filterOwners(owners: Owner[], value: string) {
        const filterValue = value.toLowerCase();

        return owners.filter(owner => owner.name.toLowerCase().includes(filterValue));
    }

    save() {
        const values = this.deviceForm.value;
        this.device.name = values.name.toString();
        this.device.isPublicRentable = values.isPublicRentable;
        this.device.maker = values.maker.toString();
        this.device.type = values.type.toString();
        this.device.serial = values.serial.toString();
        this.device.category = new Category(-1, values.category.toString());
        this.device.location = new Location(-1, values.location.toString());
        this.device.barcode = values.barcode.toString();
        this.device.textIdentifier = values.textIdentifier.toString();
        this.device.weight = values.weight;
        this.device.value = values.value;
        this.device.quantity = values.quantity;
        this.device.aquiredFrom = values.acquiredFrom;
        this.device.dateOfAcquisition = new Date(values.dateOfAcquisition);
        this.device.endOfWarranty = (values.endOfWarranty !== null) ? new Date(values.endOfWarranty) : null;
        this.device.owner = values.owner.toString() !== '' ? new Owner(-1, values.owner.toString()) : null;
        this.device.comment = values.comment.toString();


        if (this.device.id === -1) {
            // new
            this.deviceService.addDevice(this.device).subscribe(
                (device) => {
                    this.device = device;
                    this.activeModal.dismiss(device)
                },
                (error) => {
                    this.device.id = -1;
                    this.showNotification('Nem sikerült menteni, ütközés!', 'warning');
                });
        } else {
            // update
            this.deviceService.updateDevice(this.device as Device).subscribe(
                (device) => {
                    this.device = device;
                    this.activeModal.dismiss(device);
                },
                (error) => {
                    this.showNotification('Nem sikerült menteni, ütközés!', 'warning');
                })
        }
    }

    onStatusChange(value: string) {
        switch (value) {
            case '0':
                this.device.status = DeviceStatus.GOOD;
                break;
            case '1':
                this.device.status = DeviceStatus.NEEDS_REPAIR;
                break;
            case '2':
                this.device.status = DeviceStatus.SCRAPPED;
                break;
        }
    }

    delete(device: Device) {
        this.deviceService.deleteDevice(device).subscribe(device_ => {
            this.activeModal.dismiss('delete')
        });
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
        ticketModal.componentInstance.scannable = this.device;

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
