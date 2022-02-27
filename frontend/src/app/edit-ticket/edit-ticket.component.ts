import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {Ticket} from '../_model/Ticket';
import {UserService} from '../_services/user.service';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormGroup} from '@angular/forms';

@Component({
    selector: 'app-edit-ticket',
    templateUrl: './edit-ticket.component.html',
    styleUrls: ['./edit-ticket.component.css'],
    encapsulation: ViewEncapsulation.None,
})
export class EditTicketComponent implements OnInit {

    @Input() ticket: Ticket;
    @Input() title = 'Hibajegy szerkesztése';

    edit = false;

    deleteConfirmed = false;
    fullAccessMember = false;
    admin = false;

    ticketForm: FormGroup;

    constructor(
        public activeModal: NgbActiveModal,
        private fb: FormBuilder,
        private userService: UserService) {
    }

    ngOnInit() {
        this.userService.getCurrentUser().subscribe(user => {
            console.log(user);
            this.fullAccessMember = user.isFullAccessMember();
            this.admin = user.isAdmin();
        });

        this.ticketForm = this.fb.group({})
    }

    delete(ticket: Ticket) {
        this.activeModal.dismiss('delete');
    }

    save() {

    }
}
