import {Component, Input, OnInit} from '@angular/core';
import {Rent} from '../_model/Rent';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {UserService} from '../_services/user.service';
import {User} from '../_model/User';
import {RentService} from '../_services/rent.service';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'app-pdf-generation-modal',
    templateUrl: './pdf-generation-modal.component.html',
    styleUrls: ['./pdf-generation-modal.component.css']
})
export class PdfGenerationModalComponent implements OnInit {
    @Input() rent: Rent;

    dataFormGroup: UntypedFormGroup;
    user: User;

    constructor(private userService: UserService,
                private rentService: RentService,
                private fb: UntypedFormBuilder,
                private activeModal: NgbActiveModal) {
        this.dataFormGroup = fb.group({
            renterName: ['', Validators.required],
            renterId: ['', Validators.required]
        })

        userService.getCurrentUser().subscribe(user_ => {
            this.user = user_;

            this.dataFormGroup.setValue({
                renterName: user_.familyName + ' ' + user_.givenName,
                renterId: user_.personalId
            })
        });
    }

    ngOnInit(): void {
    }

    generate() {
        const val = this.dataFormGroup.value;
        if (val.renterName !== '' && val.renterId !== '') {
            const rentPdfRequest = `{"RentPdfRequest": {"teamName": "", "teamLeaderName": "", "renterFullName": "${val.renterName}", "renterId": "${val.renterId}"}}`;
            this.rentService.getPdf(this.rent, rentPdfRequest);
            this.activeModal.dismiss('generated');
        }
    }
}
