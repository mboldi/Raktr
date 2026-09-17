import {Component, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {MatPaginatorIntl, PageEvent} from '@angular/material/paginator';
import {HunPaginator} from '../helpers/hun-paginator';
import {UntypedFormControl} from '@angular/forms';
import {Ticket} from '../_model/Ticket';
import {EditTicketComponent} from '../edit-ticket/edit-ticket.component';
import {TicketService} from '../_services/ticket.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import { Location as RouterLocation } from '@angular/common' ;
import {tick} from "@angular/core/testing";
import {ActivatedRoute, Router} from "@angular/router";
import * as $ from "jquery";

@Component({
    selector: 'app-tickets',
    templateUrl: './tickets.component.html',
    styleUrls: ['./tickets.component.css'],
    providers: [Title,
        {provide: MatPaginatorIntl, useClass: HunPaginator}
    ],
})
export class TicketsComponent implements OnInit {

    activeTab = 'open'
    searchControl = new UntypedFormControl();

    pagedTickets: Ticket[];
    filteredTickets: Ticket[] = [];
    currPageIndex = 0;
    private currPageSize = 10;
    private tickets: Ticket[];

    constructor(
        private title: Title,
        private ticketService: TicketService,
        private modalService: NgbModal,
        private route: ActivatedRoute,
        private router: Router,
        private routerLocation: RouterLocation
    ) {
        this.title.setTitle('Raktr - Hibajegyek');

        ticketService.getTickets().subscribe(tickets => {
            this.tickets = this.sortTickets(tickets);

            this.filteredTickets = this.tickets;
            this.pagedTickets = this.tickets;

            this.setTicketsPage()
        });
    }

    ngOnInit(): void {
        this.searchControl.valueChanges.subscribe(value => {
            value = value.toLowerCase();
            this.filteredTickets = this.tickets.filter(ticket =>
                ticket.scannableOfProblem.name.toLowerCase().includes(value) ||
                ticket.body.toLowerCase().includes(value)
            )

            this.setTicketsPage();
        });

        // opening ticket if ID in URL is present

        if (this.route.snapshot.paramMap.get('id') !== null) {
            const id = this.route.snapshot.paramMap.get('id') as unknown as number;

            this.ticketService.getTicket(id).subscribe(ticket => {
                this.editTicket(ticket);
            }, error => {
                this.showNotification('Nem találtam eszközt az URL-ben megadott ID-vel!', 'danger');
                this.router.navigateByUrl('/devices');
            });

        }
    }

    setTab(tab: string) {
        this.activeTab = tab;
    }

    private setTicketsPage() {
        this.filteredTickets = this.sortTickets(this.filteredTickets);

        for (; this.filteredTickets.length < this.currPageIndex * this.currPageSize; this.currPageIndex--) {
        }

        this.pagedTickets = this.filteredTickets.slice(this.currPageIndex * this.currPageSize,
            (this.currPageIndex + 1) * this.currPageSize);
    }

    pageChanged(event: PageEvent) {
        this.currPageIndex = event.pageIndex;
        this.currPageSize = event.pageSize;

        this.setTicketsPage();
    }

    sortTickets(tickets): Ticket[] {
        return tickets.sort((a, b) => {
                return compare(new Date(a.dateOfWriting).getTime(), new Date(b.dateOfWriting).getTime(), false);
            }
        );
    }

    create() {
        const ticketModal = this.modalService.open(EditTicketComponent, {size: 'lg'});
        ticketModal.componentInstance.title = 'Új hibajegy';
        ticketModal.componentInstance.ticket = new Ticket();

        ticketModal.result.catch(reason => {
            if (reason === 'save') {
                this.ticketService.getTickets().subscribe(tickets => {
                    this.tickets = tickets;
                    this.filteredTickets = tickets;

                    this.setTicketsPage();
                });
            }
        })
    }

    editTicket(ticket: Ticket) {
        const ticketModal = this.modalService.open(EditTicketComponent, {size: 'lg'});
        ticketModal.componentInstance.title = 'Hibajegy szerkesztése';
        ticketModal.componentInstance.ticket = ticket;

        if (!this.route.snapshot.url.toString().includes(ticket.id.toString())) {
            this.routerLocation.go(`/tickets/${ticket.id}`);
        }

        ticketModal.result.catch(reason => {
            this.ticketService.getTickets().subscribe(tickets => {
                this.tickets = tickets;
                this.filteredTickets = tickets;

                this.setTicketsPage();
            });
        }).then(result => {
            this.routerLocation.go('/tickets');
        });
    }

    private showNotification(message_: string, type: string) {
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

function compare(a: number | string, b: number | string, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}
