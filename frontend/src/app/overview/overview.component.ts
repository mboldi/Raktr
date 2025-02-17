import {Component, HostListener, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {RentService} from '../_services/rent.service';
import {DeviceService} from '../_services/device.service';
import {Rent} from '../_model/Rent';
import {Device} from '../_model/Device';
import {UntypedFormControl} from '@angular/forms';
import {EditDeviceModalComponent} from '../edit-device-modal/edit-device-modal.component';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ScannableService} from '../_services/scannable.service';
import {EditCompositeModalComponent} from '../edit-composite-modal/edit-composite-modal.component';
import {CompositeItem} from '../_model/CompositeItem';
import {Router} from '@angular/router';
import {BarcodePurifier} from '../_services/barcode-purifier.service';
import {TicketService} from '../_services/ticket.service';
import {TicketStatus} from '../_model/TicketStatus';
import {EditTicketComponent} from '../edit-ticket/edit-ticket.component';
import {Ticket} from '../_model/Ticket';

@Component({
    selector: 'app-overview',
    templateUrl: './overview.component.html',
    styleUrls: ['./overview.component.css'],
    providers: [Title]
})
export class OverviewComponent implements OnInit {

    rents: Rent[] = [];
    deviceSearchFormControl = new UntypedFormControl();
    numOfActiveRents = 0;
    scannableAmount = 0;
    ticketAmount = 0;

    constructor(private title: Title,
                private rentService: RentService,
                private deviceService: DeviceService,
                private scannableService: ScannableService,
                private ticketService: TicketService,
                private router: Router,
                private modalService: NgbModal) {
        this.title.setTitle('Raktr - Áttekintés');
    }

    ngOnInit(): void {
        this.rentService.getRents().subscribe(rents => {
                this.rents = rents;

                this.rents = this.rents.sort((a, b) => {
                    return b.outDate.getTime() - a.outDate.getTime()
                });
            }
        );

        this.scannableService.getScannableAmount().subscribe(amount => this.scannableAmount = amount);

        this.ticketService.getTickets().subscribe(tickets => this.ticketAmount = tickets.filter(ticket => ticket.status !== TicketStatus.CLOSED).length);
    }

    activeRents(): Rent[] {
        return this.rents.filter(rent => !rent.isClosed);
    }

    searchScannable() {
        let barcode = this.deviceSearchFormControl.value;

        if (barcode === '') {
            this.showNotification('Kérlek adj meg egy vonalkódot!', 'warning');
        } else {
            barcode = BarcodePurifier.purify(barcode);

            this.scannableService.getScannableByBarcode(barcode).subscribe(scannable => {
                    if (scannable === undefined) {
                        this.showNotification('Nem találtam eszközt ilyen vonalkóddal!', 'warning');
                    } else if (scannable['type_'] === 'device') {
                        const editModal = this.modalService.open(EditDeviceModalComponent, {size: 'lg'});
                        editModal.componentInstance.title = 'Eszköz szerkesztése';
                        editModal.componentInstance.device = scannable as Device;
                    } else if (scannable['type_'] === 'compositeItem') {
                        const editModal = this.modalService.open(EditCompositeModalComponent, {size: 'lg'});
                        editModal.componentInstance.title = 'Összetett eszköz szerkesztése';
                        editModal.componentInstance.compositeItem = scannable as CompositeItem;
                    }
                },
                (error => {
                    this.showNotification('Nem találtam eszközt ilyen vonalkóddal!', 'warning');
                }));
        }

        this.deviceSearchFormControl.setValue('');
    }

    openRent(id: number) {
        this.router.navigateByUrl('/rent/' + id);
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

    @HostListener('document:keyDown.control.k', ['$event'])
    selectPack(event: KeyboardEvent) {
        event.preventDefault();

        this.router.navigateByUrl('rent/new');
    }

    newDevice() {
        const editModal = this.modalService.open(EditDeviceModalComponent, {size: 'lg'});
        editModal.componentInstance.title = 'Új eszköz';
        editModal.componentInstance.device = new Device();
        editModal.componentInstance.device.id = -1;

        editModal.result.catch(() => {
            this.scannableService.getScannableAmount().subscribe(amount => this.scannableAmount = amount);
        })
    }

    newRent() {
        this.router.navigateByUrl('/rent/new');
    }

    newTicket() {
        const ticketModal = this.modalService.open(EditTicketComponent, {size: 'lg'});
        ticketModal.componentInstance.title = 'Új hibajegy';
        ticketModal.componentInstance.ticket = new Ticket();

        ticketModal.result.catch(reason => {
            if (reason === 'save') {
                this.ticketService.getTickets().subscribe(tickets => this.ticketAmount = tickets.filter(ticket => ticket.status !== TicketStatus.CLOSED).length);
            }
        })
    }
}
