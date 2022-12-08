import {Component, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {MatPaginatorIntl, PageEvent} from '@angular/material/paginator';
import {HunPaginator} from '../helpers/hun-paginator';
import {FormControl} from '@angular/forms';
import {Ticket} from '../_model/Ticket';
import {EditTicketComponent} from '../edit-ticket/edit-ticket.component';
import {TicketService} from '../_services/ticket.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

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
    searchControl = new FormControl();

    pagedTickets: Ticket[];
    filteredTickets: Ticket[];
    currPageIndex = 0;
    private currPageSize = 10;
    private tickets: Ticket[]

    constructor(
        private ticketService: TicketService,
        private modalService: NgbModal
    ) {
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
        })
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
        const ticketModal = this.modalService.open(EditTicketComponent, {size: 'lg', backdrop: false});
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
        const ticketModal = this.modalService.open(EditTicketComponent, {size: 'lg', backdrop: false});
        ticketModal.componentInstance.title = 'Hibajegy szerkesztése';
        ticketModal.componentInstance.ticket = ticket;

        ticketModal.result.catch(reason => {
            this.ticketService.getTickets().subscribe(tickets => {
                this.tickets = tickets;
                this.filteredTickets = tickets;

                this.setTicketsPage();
            });
        })
    }
}

function compare(a: number | string, b: number | string, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}
