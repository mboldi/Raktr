import {Component, Inject} from '@angular/core';
import {MatButton} from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {FormControl, ReactiveFormsModule, Validators} from '@angular/forms';

export class QuantityInputDialogData {
  deviceName: string;
  maxQuantity: number;

  constructor(deviceName: string, maxQuantity: number) {
    this.deviceName = deviceName;
    this.maxQuantity = maxQuantity;
  }
}

@Component({
  selector: 'app-quantity-input-modal',
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    MatInput,
    MatLabel,
    MatSuffix,
    ReactiveFormsModule,
    MatDialogClose
  ],
  templateUrl: './quantity-input-modal.component.html',
  styleUrl: './quantity-input-modal.component.scss',
})
export class QuantityInputModalComponent {
  protected quantityFormControl: FormControl;

  protected deviceName: string;
  protected maxQuantity: number;

  constructor(@Inject(MAT_DIALOG_DATA) protected data: QuantityInputDialogData,
              private dialogRef: MatDialogRef<QuantityInputModalComponent>) {
    this.deviceName = data.deviceName;
    this.maxQuantity = data.maxQuantity;

    this.quantityFormControl = new FormControl(1, [
      Validators.required,
      Validators.min(1),
      Validators.max(data.maxQuantity),
    ]);
  }

  protected save() {
    if (this.quantityFormControl.invalid) {
      return;
    }

    this.dialogRef.close(this.quantityFormControl.value);
  }
}
