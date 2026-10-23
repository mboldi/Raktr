import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatButton } from '@angular/material/button';
import { MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';

export interface RentPdfDialogData {
  /** The logged-in user's own name and ID number - the ID is null when they haven't set one. */
  ownName: string;
  ownId: string | null;
  /** The renter named on the rent itself - prefilled, but editable; their ID number has to be typed in. */
  rentRenterName: string;
}

export interface RentPdfDialogResult {
  renterName: string;
  renterId: string;
}

type DataSource = 'own' | 'renter';

@Component({
  selector: 'app-rent-pdf-modal',
  imports: [
    ReactiveFormsModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
    MatButton,
    MatRadioGroup,
    MatRadioButton,
    MatFormField,
    MatLabel,
    MatInput,
  ],
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './rent-pdf-modal.component.html',
  styleUrl: './rent-pdf-modal.component.scss',
})
export class RentPdfModalComponent {
  protected data = inject<RentPdfDialogData>(MAT_DIALOG_DATA);
  private dialogRef =
    inject<MatDialogRef<RentPdfModalComponent, RentPdfDialogResult>>(MatDialogRef);

  protected source = new FormControl<DataSource>(this.data.ownId ? 'own' : 'renter', {
    nonNullable: true,
  });
  protected renterNameControl = new FormControl(this.data.rentRenterName, { nonNullable: true });
  protected renterIdControl = new FormControl('', { nonNullable: true });

  protected get canConfirm(): boolean {
    return (
      this.source.value === 'own' ||
      (this.renterNameControl.value.trim().length > 0 &&
        this.renterIdControl.value.trim().length > 0)
    );
  }

  protected confirm() {
    if (!this.canConfirm) {
      return;
    }

    if (this.source.value === 'own') {
      this.dialogRef.close({ renterName: this.data.ownName, renterId: this.data.ownId! });
    } else {
      this.dialogRef.close({
        renterName: this.renterNameControl.value.trim(),
        renterId: this.renterIdControl.value.trim(),
      });
    }
  }
}
