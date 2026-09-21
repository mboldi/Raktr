import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatInput, MatLabel, MatSuffix } from '@angular/material/input';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';

export class QuantityInputDialogData {
  deviceName: string;
  maxQuantity: number;
  initialQuantity: number;
  minQuantity: number;
  promptText: string;
  confirmLabel: string;

  constructor(
    deviceName: string,
    maxQuantity: number,
    initialQuantity = 1,
    minQuantity = 1,
    promptText?: string,
    confirmLabel = 'Hozzáadás',
  ) {
    this.deviceName = deviceName;
    this.maxQuantity = maxQuantity;
    this.initialQuantity = initialQuantity;
    this.minQuantity = minQuantity;
    this.promptText =
      promptText ?? `${deviceName} - hány darabot adsz hozzá? (elérhető: ${maxQuantity} db)`;
    this.confirmLabel = confirmLabel;
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
    MatDialogClose,
  ],
  templateUrl: './quantity-input-modal.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './quantity-input-modal.component.scss',
})
export class QuantityInputModalComponent {
  protected data = inject<QuantityInputDialogData>(MAT_DIALOG_DATA);
  private dialogRef = inject<MatDialogRef<QuantityInputModalComponent>>(MatDialogRef);

  protected quantityFormControl: FormControl;

  protected deviceName: string;
  protected maxQuantity: number;
  protected promptText: string;
  protected confirmLabel: string;

  constructor() {
    const data = this.data;

    this.deviceName = data.deviceName;
    this.maxQuantity = data.maxQuantity;
    this.promptText = data.promptText;
    this.confirmLabel = data.confirmLabel;

    this.quantityFormControl = new FormControl(data.initialQuantity, [
      Validators.required,
      Validators.min(data.minQuantity),
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
