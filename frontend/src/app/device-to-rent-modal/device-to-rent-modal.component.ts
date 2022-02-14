import {Component, Input, OnInit, Output} from '@angular/core';
import {Device} from '../model/Device';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'app-device-to-rent-modal',
  templateUrl: './device-to-rent-modal.component.html',
  styleUrls: ['./device-to-rent-modal.component.css']
})
export class DeviceToRentModalComponent implements OnInit {
  @Input() device: Device;
  amountFormControl = new FormControl();

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
    this.amountFormControl.setValue(1);
  }

  finish() {
    this.activeModal.dismiss(this.amountFormControl.value);
  }
}
