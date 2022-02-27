import {Component, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {MatPaginatorIntl, PageEvent} from '@angular/material/paginator';
import {HunPaginator} from '../helpers/hun-paginator';
import {FormControl} from '@angular/forms';
import {Ticket} from '../_model/Ticket';
import {Sort} from '@angular/material/sort';
import {User} from '../_model/User';
import {ProblemSeverity} from '../_model/ProblemSeverity';
import {Device} from '../_model/Device';
import {TicketStatus} from '../_model/TicketStatus';
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
    private currPageSize = 25;
    private sortedTickets: Ticket[];
    private tickets = [new Ticket(0, 'asdasdasdasd',
        new Date(), new User(0, 'asdsad', 'asasdasdd', 'asd', 'asd', 'sad'),
        TicketStatus.OPEN, new Device(0, 'eszköz'), ProblemSeverity.SEVERE)];

    constructor(
        private ticketService: TicketService,
        private modalService: NgbModal
    ) {
        this.filteredTickets = this.tickets;
        this.pagedTickets = this.tickets;
    }

    ngOnInit(): void {
    }

    setTab(tab: string) {
        this.activeTab = tab;
    }

    private setTicketsPage() {
        for (; this.sortedTickets.length < this.currPageIndex * this.currPageSize; this.currPageIndex--) {
        }

        this.pagedTickets = this.sortedTickets.slice(this.currPageIndex * this.currPageSize,
            (this.currPageIndex + 1) * this.currPageSize);
    }

    pageChanged(event: PageEvent) {
        this.currPageIndex = event.pageIndex;
        this.currPageSize = event.pageSize;

        this.setTicketsPage();
    }

    sortTickets(sort: Sort) {
        if (this.tickets.length === 0) {
            return;
        }
        const data = this.tickets.slice();
        if (!sort.active || sort.direction === '') {
            this.sortedTickets = data;
            return;
        }

        this.sortedTickets = data.sort((a, b) => {
            const isAsc = sort.direction === 'asc';
            switch (sort.active) {
                case 'name':
                    return compare(a.dateOfWriting.getTime(), b.dateOfWriting.getTime(), isAsc);
                default:
                    return 0;
            }
        });

        this.setTicketsPage();
    }

    create() {
        const ticketModal = this.modalService.open(EditTicketComponent, {size: 'lg', backdrop: false});
        ticketModal.componentInstance.title = 'Új hibajegy';
        ticketModal.componentInstance.ticket = new Ticket();
    }

    editTicket(ticket: Ticket) {
        const ticketModal = this.modalService.open(EditTicketComponent, {size: 'lg', backdrop: false});
        ticketModal.componentInstance.title = 'Hibajegy szerkesztése';
        ticketModal.componentInstance.ticket = ticket;

        ticketModal.result.catch(reason => {
            if (reason === 'delete') {
                this.ticketService.getTickets().subscribe(tickets => {
                    this.tickets = tickets;
                    this.sortedTickets = tickets;

                    this.setTicketsPage();
                });
            }
        })
    }
}

function compare(a: number | string, b: number | string, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}
