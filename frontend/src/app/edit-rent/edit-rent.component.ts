import * as $ from 'jquery';
import {Rent} from '../model/Rent';
import {RentService} from '../_services/rent.service';
import {Title} from '@angular/platform-browser';
import {Component, ElementRef, HostListener, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {RentItem} from '../model/RentItem';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DeviceService} from '../_services/device.service';
import {Device} from '../model/Device';
import {DeviceToRentModalComponent} from '../device-to-rent-modal/device-to-rent-modal.component';
import {Scannable} from '../model/Scannable';
import {BackStatus} from '../model/BackStatus';
import {ScannableService} from '../_services/scannable.service';
import {UserService} from '../_services/user.service';
import {CompositeItem} from '../model/CompositeItem';
import {MatCheckboxChange} from '@angular/material/checkbox';
import {PdfGenerationModalComponent} from '../pdf-generation-modal/pdf-generation-modal.component';
import {BarcodePurifier} from '../_services/barcode-purifier.service';
import {RentType} from '../model/RentType';
import {MatDatepickerInputEvent} from '@angular/material/datepicker';
import {User} from '../model/User';
import {Comment} from '../model/Comment';

@Component({
    selector: 'app-edit-rent',
    templateUrl: './edit-rent.component.html',
    styleUrls: ['./edit-rent.component.css']
})
export class EditRentComponent implements OnInit {
    @ViewChild('barcodeInput') input: ElementRef;

    rentIssuingMembers: User[];
    filteredRentIssuingMembers: User[];
    selectedIssuer = '';

    rent: Rent;
    filteredRentItems: RentItem[];
    currentOutDate: Date = new Date();

    searchControl = new FormControl();
    addRentFormControl = new FormControl();
    rentDataForm: FormGroup;
    newCommentForm: FormGroup;
    fullAccessMember = false;
    admin = false;
    user: User;

    deleteConfirmed = false;
    whyNotFinalizable = 'Hiányzó adatok a lezáráshoz!';
    barcodePlaceholder = 'Hozzáadandó vonalkódja';
    barcodeMode = 'add';

    newComment = '';

    constructor(private rentService: RentService,
                private deviceService: DeviceService,
                private scannableService: ScannableService,
                private userService: UserService,
                private title: Title,
                private route: ActivatedRoute,
                private modalService: NgbModal,
                private fb: FormBuilder,
                private router: Router) {
        this.title.setTitle('Raktr - Kivitel szerkesztése');

        this.rentDataForm = fb.group({
            rentType: ['SIMPLE'],
            destination: ['', Validators.required],
            issuer: ['', Validators.required],
            renter: ['', Validators.required],
            outDate: ['', Validators.required],
            expBackDate: ['', Validators.required],
            actBackDate: ['']
        })

        this.newCommentForm = fb.group(
            {
                commentBody: ['', Validators.required]
            })

        this.userService.getCurrentUser().subscribe(user => {
            this.user = user;

            this.fullAccessMember = User.isFullAccessMember(user);
            this.admin = User.isAdmin(user);

            if (this.route.snapshot.paramMap.get('id') === 'new') {
                this.rent.issuer = this.user;

                this.rentDataForm.patchValue({
                    'issuer': this.user.familyName + ' ' + this.user.givenName
                })

                this.selectedIssuer = this.user.username;
            }
        });

        userService.getRentIssuerableMembers().subscribe(users => {
            this.rentIssuingMembers = users;
        })

        this.rentDataForm
            .get('issuer')
            .valueChanges
            .subscribe(
                value => {
                    const filteredMembers =
                        this.rentIssuingMembers.filter(user => (user.familyName + ' ' + user.givenName)
                            .toLowerCase().includes(value.toLowerCase()));

                    this.filteredRentIssuingMembers = filteredMembers.length > 10 ? [] : filteredMembers;
                });
    }

    ngOnInit(): void {
        const id = this.route.snapshot.paramMap.get('id');

        if (id === 'new') {
            this.rent = new Rent();

            this.rent.type = RentType.SIMPLE;
            this.rent.isClosed = false;

            this.setFormFieldsWithRentData();
        } else {
            this.rentService.getRent(id).subscribe(rent => {
                    this.rent = rent;

                    this.setFormFieldsWithRentData();
                    this.sortComments();

                    this.filteredRentItems = this.rent.rentItems;
                    this.currentOutDate = this.rent.outDate;

                    this.rentDataForm.get('issuer').disable();
                    this.rentDataForm.get('renter').disable();
                }
            )
        }

    }

    private setFormFieldsWithRentData() {
        this.rentDataForm.setValue({
            rentType: this.rent.type === RentType.SIMPLE ? 'SIMPLE' : 'COMPLEX',
            destination: this.rent.destination,
            issuer: this.rent.issuer.familyName + ' ' + this.rent.issuer.givenName,
            renter: this.rent.renter,
            outDate: this.rent.outDate,
            expBackDate: this.rent.expBackDate,
            actBackDate: this.rent.backDate
        });
    }

    private _filterRentItems(rentItems_: RentItem[], value: string): RentItem[] {
        const filterValue = value.toLowerCase();

        return rentItems_.filter(rentItem =>
            rentItem.scannable.name.toLowerCase().includes(filterValue) ||
            rentItem.scannable.barcode.toLowerCase().includes(filterValue) ||
            rentItem.scannable.textIdentifier.toLowerCase().includes(filterValue) ||
            rentItem.scannable.category.name.toLowerCase().includes(filterValue) ||
            rentItem.scannable.location.name.toLowerCase().includes(filterValue));
    }

    barcodeRead() {
        if (this.addRentFormControl.value !== null && this.addRentFormControl.value !== '') {
            const barcode = BarcodePurifier.purify(this.addRentFormControl.value)

            if (this.barcodeMode === 'add') {
                this.scannableService.getScannableByBarcode(barcode).subscribe(scannable => {
                        if (scannable === undefined) {
                            this.showNotification('Nem találtam eszközt ilyen vonalkóddal!', 'warning');
                        } else if (scannable['type_'] === 'device') {
                            const device = scannable as Device;

                            if (device.quantity > 1) {
                                const editModal = this.modalService.open(DeviceToRentModalComponent, {size: 'md', windowClass: 'modal-holder'});
                                editModal.componentInstance.device = device;

                                editModal.result.catch(result => {
                                    if (result !== null && result as number !== 0) {
                                        this.addScannableToRent(device, result);
                                    }
                                })
                            } else {
                                this.addScannableToRent(device, 1);
                            }
                        } else if (scannable['type_'] === 'compositeItem') {
                            const composite = scannable as CompositeItem;
                            this.addScannableToRent(composite, 1);
                        } else {
                            this.showNotification('Nem találok ilyet!', 'warning');
                        }
                    },
                    error => {
                        this.showNotification('Nem találtam eszközt ilyen vonalkóddal!', 'warning');
                    });
            } else if (this.barcodeMode === 'pack') {
                const rentItemToChange = this.rent.rentItems.find(item => item.scannable.barcode === barcode);

                if (rentItemToChange === undefined) {
                    this.showNotification('Nem találtam eszközt a listában ilyen vonalkóddal!', 'warning');
                } else {
                    if (rentItemToChange.backStatus === BackStatus.OUT) {
                        this.showNotification(`${rentItemToChange.scannable.name} ár kinn!`, 'warning');
                    } else {
                        this.togglePackedStatus(rentItemToChange);
                    }
                }
            } else if (this.barcodeMode === 'back') {
                const rentItemToChange = this.rent.rentItems.find(item => item.scannable.barcode === barcode);

                if (rentItemToChange === undefined) {
                    this.showNotification('Nem találtam eszközt a listában ilyen vonalkóddal!', 'warning');
                } else {
                    if (rentItemToChange.backStatus === BackStatus.BACK) {
                        this.showNotification(`${rentItemToChange.scannable.name} már visszavéve!`, 'warning');
                    } else {
                        this.toggleBackStatus(rentItemToChange);
                    }
                }
            }

            this.addRentFormControl.setValue('');
        } else {
            this
                .showNotification(
                    'Add meg a vonalkódot!'
                    ,
                    'warning'
                );
        }
    }

    addScannableToRent(scannable: Scannable, amount: number) {
        let newRentItem = null;

        this.rent.rentItems.forEach(rentItem => {
            if (rentItem.scannable.barcode === scannable.barcode) {
                if (rentItem.outQuantity !== amount) {
                    rentItem.outQuantity = amount;
                    this.showNotification(scannable.name + ' mennyisége frissítve: ' + amount + 'db', 'success');
                } else {
                    this.showNotification('Ez az eszköz (' + scannable.name + ') már hozzá van adva ilyen mennyiségben', 'warning');
                }
                newRentItem = rentItem;
            }
        });

        if (newRentItem === null) {
            newRentItem = new RentItem(undefined,
                scannable,
                this.rent.type === RentType.SIMPLE ? BackStatus.OUT : BackStatus.PLANNED,
                amount);

            this.rentService.addItemToRent(this.rent.id, newRentItem).subscribe(rent_ => {
                this.rentService.getRent(this.rent.id).subscribe(rent => {
                    if (rent === undefined) {
                        this.showNotification('Nem sikerült hozzáadni', 'warning');
                    } else {
                        this.rent = rent;

                        if (amount > 1) {
                            this.showNotification(amount + ' darab ' + scannable.name + ' hozzáadva sikeresen!', 'success');
                        } else {
                            this.showNotification(scannable.name + ' hozzáadva sikeresen!', 'success');
                        }

                        this.searchControl.setValue('');
                    }
                })
            }, error => {
                if (error.status === 409) {
                    this.showNotification('Nem lehetséges ennyit bérelni ebből az eszközből', 'warning');
                } else {
                    this.showNotification('Nem sikerült hozzáadni!', 'warning')
                }
            })
        }

    }

    removeFromRent(rentItem: RentItem) {
        rentItem.outQuantity = 0;
        this.rentService.updateInRent(this.rent.id, rentItem).subscribe(rent_ => {
                this.rentService.getRent(this.rent.id).subscribe(rent => {
                    this.rent = rent;
                    this.searchControl.setValue('');
                    this.showNotification(`${rentItem.scannable.name} törölve!`, 'success');
                })
            },
            error => this.showNotification('Nem sikerült törölni', 'warning'))
    }

    private setRentFields() {
        const value = this.rentDataForm.value;

        this.rent.destination = value.destination.toString();
        if (value.renter !== undefined) {
            this.rent.renter = value.renter.toString();
        }
        this.rent.type = value.rentType === 'COMPLEX' ? RentType.COMPLEX : RentType.SIMPLE;
        this.rent.outDate = value.outDate;
        this.rent.expBackDate = value.expBackDate;
        this.rent.backDate = value.actBackDate;
    }

    save() {
        this.setRentFields();

        if (this.rent.id === -1) {
            // new
            this.rentService.addRent(this.rent).subscribe(rent_ => {
                    this.rent = rent_;
                    this.showNotification('Sikeresen mentve', 'success');

                    this.rentDataForm.get('issuer').disable();
                    this.rentDataForm.get('renter').disable();

                    this.router.navigateByUrl('/rent/' + this.rent.id);
                },
                error => {
                    this.showNotification('Nem sikerült menteni', 'error');
                })
        } else {
            // update
            this.rentService.updateRent(this.rent).subscribe(rent_ => {
                    this.rent = rent_;
                    this.showNotification('Sikeresen mentve', 'success');
                },
                error => {
                    this.showNotification('Nem sikerült menteni', 'error');
                })
        }
    }

    delete(rent: Rent) {
        this.rentService.deleteRent(rent).subscribe(rent_ => {
                this.showNotification('Bérlés törölve', 'success');
                this.router.navigateByUrl('/rents')
            }
        )
    }

    private togglePackedStatus(rentItem: RentItem) {
        if (rentItem.backStatus !== BackStatus.BACK) {
            if (rentItem.backStatus === BackStatus.PLANNED) {
                rentItem.backStatus = BackStatus.OUT;
            } else {
                rentItem.backStatus = BackStatus.PLANNED;
            }

            this.rentService.updateInRent(this.rent.id, rentItem).subscribe(
                rent => {
                    this.rent = rent;
                    this.filteredRentItems.find(item => item.id === rentItem.id).backStatus = rentItem.backStatus;
                    this.showNotification('Sikeresen mentve!', 'success');
                },
                error => {
                    this.showNotification('Nem sikerült menteni :(', 'warning');
                }
            )
        } else {
            rentItem.backStatus = BackStatus.BACK;
            this.filteredRentItems.find(item => item.id === rentItem.id).backStatus = rentItem.backStatus;
            this.showNotification('Már visszajött, nem tudod nem elpakolni :D', 'warning')
        }
    }

    packedChanged(checkboxChange: MatCheckboxChange, rentItem: RentItem) {
        this.togglePackedStatus(rentItem);

        checkboxChange.source.checked = rentItem.backStatus === BackStatus.BACK ||
            rentItem.backStatus === BackStatus.OUT;
    }

    private toggleBackStatus(rentItem: RentItem) {
        if ((rentItem.backStatus === BackStatus.OUT || this.rent.type === RentType.SIMPLE) ||
            rentItem.backStatus === BackStatus.BACK) {
            if (rentItem.backStatus === BackStatus.OUT) {
                rentItem.backStatus = BackStatus.BACK;
            } else {
                rentItem.backStatus = BackStatus.OUT;
            }

            this.rentService.updateInRent(this.rent.id, rentItem).subscribe(
                rent => {
                    this.rent = rent;
                    this.filteredRentItems.find(item => item.id === rentItem.id).backStatus = rentItem.backStatus;
                    this.showNotification('Sikeresen mentve!', 'success');
                },
                error => {
                    this.showNotification('Nem sikerült menteni :(', 'warning')
                }
            )
        } else {
            rentItem.backStatus = BackStatus.PLANNED;
            this.filteredRentItems.find(item => item.id === rentItem.id).backStatus = rentItem.backStatus;
            this.showNotification('Még elpakolva sincs, hogy hoztad volna vissza!?', 'warning')
        }
    }

    backChanged(checkboxChange: MatCheckboxChange, rentItem: RentItem) {
        this.toggleBackStatus(rentItem);

        checkboxChange.source.checked = rentItem.backStatus === BackStatus.BACK;
    }

    quantityChanged(event, rentItem: RentItem) {
        const oldQuantity = rentItem.outQuantity;
        rentItem.outQuantity = event.target.value;

        this.rentService.updateInRent(this.rent.id, rentItem).subscribe(rent => {
                this.showNotification(rentItem.scannable.name + ' mennyisége frissítve: ' + rentItem.outQuantity + 'db', 'success');
            },
            error => {
                this.showNotification('Nem lehet ilyen mennyiségben kivinni!', 'warning');
                event.target.value = oldQuantity;
            }
        )
    }

    isFinalizable() {
        if (this.rent.id === -1) {
            this.whyNotFinalizable = 'Nincs mit véglegesíteni!';
            return false;
        }

        if (this.rent.backDate === null) {
            this.whyNotFinalizable = 'Nincs megadva visszaérkezési dátum!';
            return false;
        }

        for (let i = 0; i < this.rent.rentItems.length; i++) {
            if (this.rent.rentItems[i].backStatus !== BackStatus.BACK) {
                this.whyNotFinalizable = 'Nem érkezett minden vissza!';
                return false;
            }
        }

        this.whyNotFinalizable = 'Véglegesíthető';

        return true;
    }

    close() {
        this.setRentFields();
        this.rent.isClosed = true;

        this.rentService.finalizeRent(this.rent).subscribe(rent => {
            this.rent = rent;
        });
    }

    open() {
        this.setRentFields();
        this.rent.isClosed = false;

        this.rentService.finalizeRent(this.rent).subscribe(rent => {
            this.rent = rent;
        });
    }

    getPdf() {
        const pdfModal = this.modalService.open(PdfGenerationModalComponent, {size: 'md', windowClass: 'modal-holder'});
        pdfModal.componentInstance.rent = this.rent;
    }

    setCurrOutDate(event) {
        this.currentOutDate = new Date(event.value);
    }

    setActBackDate($event: MatDatepickerInputEvent<Date>) {
        this.rent.backDate = this.rentDataForm.value.actBackDate;
    }

    sendComment() {
        const commentBody = this.newCommentForm.value.commentBody;

        if (commentBody !== '') {
            const newComment = new Comment(
                -1,
                commentBody,
                new Date(),
                this.user
            )

            this.rentService.addComment(this.rent, newComment).subscribe(rent => {
                this.newCommentForm.setValue({commentBody: ''});

                this.rent = rent;
                this.sortComments();
            })
        }
    }

    deleteComment(comment: Comment) {
        this.rentService.deleteComment(this.rent, comment).subscribe(rent => {
            this.rent = rent;
        })
    }

    private sortComments() {
        this.rent.comments.sort((a, b) => a.dateOfWriting > b.dateOfWriting ? -1 : 1)
    }

    pack($event: MouseEvent) {
        if (this.barcodeMode === 'pack') {
            this.selectBarcodeMode('add')
        } else {
            this.selectBarcodeMode('pack');
        }
    }

    back($event: MouseEvent) {
        if (this.barcodeMode === 'back') {
            this.selectBarcodeMode('add')
        } else {
            this.selectBarcodeMode('back');
        }
    }

    filterRentItems($event: any) {
        this.filteredRentItems = this._filterRentItems(this.rent.rentItems, $event);
    }

    showNotification(message_: string, type: string) {
        $['notify']({
            icon: 'add_alert',
            message: message_
        }, {
            type: type,
            timer: 500,
            placement: {
                from: 'top',
                align: 'right'
            },
            z_index: 2000
        })
    }

    @HostListener('document:keyDown.control.a', ['$event'])
    selectAdd(event: KeyboardEvent) {
        event.preventDefault();

        this.selectBarcodeMode('add');
    }

    @HostListener('document:keyDown.control.b', ['$event'])
    selectBack(event: KeyboardEvent) {
        event.preventDefault();

        this.selectBarcodeMode('back');
    }

    @HostListener('document:keyDown.control.p', ['$event'])
    selectPack(event: KeyboardEvent) {
        event.preventDefault();

        this.selectBarcodeMode('pack');
    }

    private selectBarcodeMode(mode: string) {
        this.barcodeMode = mode;

        switch (mode) {
            case 'add':
                this.barcodePlaceholder = 'Hozzáadandó vonalkódja';
                break;
            case 'pack':
                this.barcodePlaceholder = 'Elpakolt eszköz vonalkódja';
                break;
            case 'back':
                this.barcodePlaceholder = 'Visszavett eszköz vonalkódja';
                break;
        }

        this.input.nativeElement.focus();
    }

    setSelectedIssuer(username: string) {
        this.selectedIssuer = username;

        this.userService.getUser(username).subscribe(user => this.rent.issuer = user);
    }
}
