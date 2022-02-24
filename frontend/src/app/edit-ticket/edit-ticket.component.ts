import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {Ticket} from '../_model/Ticket';
import {UserService} from '../_services/user.service';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {User} from '../_model/User';

@Component({
    selector: 'app-edit-ticket',
    templateUrl: './edit-ticket.component.html',
    styleUrls: ['./edit-ticket.component.css'],
    encapsulation: ViewEncapsulation.None,
})
export class EditTicketComponent implements OnInit {

    @Input() ticket: Ticket;

    fullAccessMember = false;

    title = 'Hibajegy szerkesztése';
    ticketForm: FormGroup;

    constructor(
        public activeModal: NgbActiveModal,
        private fb: FormBuilder,
        private userService: UserService) {

        if (this.ticket === undefined) {
            this.ticket = new Ticket();
        }

        this.userService.getCurrentUser().subscribe(user => {
            this.fullAccessMember = User.isFullAccessMember(user);
        });
    }

    ngOnInit() {
        if (this.ticket.id === -1) {
            this.title = 'Új hibajegy létrehozása';
        }
    }
}
