import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'app-yesno-modal',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogClose
  ],
  templateUrl: './yesno-modal.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './yesno-modal.component.scss',
})
export class YesnoModalComponent {  protected title = inject(MAT_DIALOG_DATA);


}
