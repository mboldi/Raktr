import {Component, Input, OnInit} from '@angular/core';
import {Device} from '../_model/Device';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {UntypedFormControl} from '@angular/forms';
import * as $ from 'jquery';

@Component({
    selector: 'app-device-to-rent-modal',
    templateUrl: './device-to-rent-modal.component.html',
    styleUrls: ['./device-to-rent-modal.component.css']
})
export class DeviceToRentModalComponent implements OnInit {
    @Input() device: Device;
    amountFormControl = new UntypedFormControl();

    constructor(public activeModal: NgbActiveModal) {
    }

    ngOnInit(): void {
        this.amountFormControl.setValue(1);
    }

    finish() {
        if (this.amountFormControl.value <= this.device.quantity) {
            this.activeModal.dismiss(this.amountFormControl.value);
        } else {
            this.showNotification(`Az elérhető mennyiség csak ${this.device.quantity}`, 'warning');
            this.amountFormControl.setValue(this.device.quantity);
        }
    }

    keydown($event: KeyboardEvent) {
        if ($event.key === 'Escape') {
            this.activeModal.dismiss(null);
        }
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
}
