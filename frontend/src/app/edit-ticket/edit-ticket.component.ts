import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {Ticket} from '../_model/Ticket';
import {UserService} from '../_services/user.service';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators} from '@angular/forms';
import {ScannableService} from '../_services/scannable.service';
import * as $ from 'jquery';
import {Scannable} from '../_model/Scannable';
import {Device} from '../_model/Device';
import {Comment} from '../_model/Comment';
import {CompositeItem} from '../_model/CompositeItem';
import {User} from '../_model/User';
import {TicketStatus} from '../_model/TicketStatus';
import {TicketService} from '../_services/ticket.service';
import {Title} from '@angular/platform-browser';

@Component({
    selector: 'app-edit-ticket',
    templateUrl: './edit-ticket.component.html',
    styleUrls: ['./edit-ticket.component.css'],
    encapsulation: ViewEncapsulation.None,
})
export class EditTicketComponent implements OnInit {

    @Input() ticket: Ticket;
    @Input() title: string;
    @Input() scannable: Scannable;

    protected readonly TicketStatus = TicketStatus;

    edit = false;

    deleteConfirmed = false;

    currUser: User;
    fullAccessMember = false;
    admin = false;

    ticketForm: UntypedFormGroup;
    scannableSearchControl = new UntypedFormControl();
    showScannableLoading = false;
    newCommentFormControl = new UntypedFormControl();
    closingComment = false;

    constructor(
        public activeModal: NgbActiveModal,
        private pageTitle: Title,
        private fb: UntypedFormBuilder,
        private scannableService: ScannableService,
        private ticketService: TicketService,
        private userService: UserService) {
        this.pageTitle.setTitle('Raktr - Kivitel szerkesztése');

        if (this.ticket === null || this.ticket === undefined) {
            this.ticket = new Ticket();
        }

    }

    ngOnInit() {
        if (this.scannable !== null && this.scannable !== undefined) {
            this.ticket.scannableOfProblem = this.scannable;
        }

        this.userService.getCurrentUser().subscribe(user => {
            this.currUser = user;

            this.fullAccessMember = user.isFullAccessMember();
            this.admin = user.isAdmin();
        });

        this.scannableSearchControl.setValue('');

        this.ticketForm = this.fb.group({
            severity: ['SEVERE', Validators.required],
            body: ['', Validators.required]
        })

        this.sortComments();
    }

    sortComments() {
        this.ticket.comments = this.ticket.comments.sort((a, b) => {
                return compare(new Date(b.dateOfWriting).getTime(), new Date(a.dateOfWriting).getTime(), false);
            }
        );
    }

    delete(ticket: Ticket) {
        this.activeModal.dismiss('delete');
    }

    save() {
        const formValue = this.ticketForm.value;
        this.ticket.severity = formValue.severity;
        this.ticket.body = formValue.body;
        this.ticket.dateOfWriting = new Date();
        this.ticket.writer = this.currUser;
        this.ticket.status = TicketStatus.OPEN;

        this.ticketService.addTicket(this.ticket).subscribe(ticket => {
            this.ticket = ticket;
            this.activeModal.dismiss('save');
        })

        this.sortComments();
    }

    searchScannable() {
        this.showScannableLoading = true;

        const value = this.scannableSearchControl.value;

        if (value.length === 7 && !isNaN(Number(value))) {
            this.scannableService.getScannableByBarcode(value).subscribe(scannable => {
                this.showScannableLoading = false;
                this.ticket.scannableOfProblem = scannable;

            }, error => {
                this.showScannableLoading = false;
                this.showNotification('Nem találtam eszközt ilyen vonalkóddal', 'warning');
            })
        } else {
            console.log('nem kód :(');
        }
    }

    asDevice(scannable: Scannable): Device {
        return scannable as Device;
    }

    asComposite(scannable: Scannable): CompositeItem {
        return scannable as CompositeItem;
    }

    sendComment() {
        const newCommentBody = this.newCommentFormControl.value;

        const newComment = new Comment(-1, newCommentBody, new Date(), this.currUser);

        this.ticketService.addCommentToTicket(this.ticket, newComment).subscribe(ticket => {
            this.ticket = ticket;

            let change = false;

            if (this.closingComment === true) {
                this.ticket.status = TicketStatus.CLOSED;
                change = true;
            } else if (this.ticket.status !== TicketStatus.WORKING_ON_IT) {
                this.ticket.status = TicketStatus.WORKING_ON_IT;
                change = true;
            }

            if (change) {
                this.ticketService.updateTicket(this.ticket).subscribe(updatedTicket => {
                    this.ticket = updatedTicket;
                })
            }

            this.newCommentFormControl.setValue('');
            this.sortComments();
        })
    }

    deleteComment(comment) {

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

function compare(a: number | string, b: number | string, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}
