import {Component, Inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {FormBuilder, ReactiveFormsModule, UntypedFormGroup, Validators} from "@angular/forms";
import {OwnerUpdateDto} from "../../model/owner/ownerUpdateDto";
import {MatCheckbox} from "@angular/material/checkbox";
import {OwnerCreateDto} from "../../model/owner/ownerCreateDto";
import {OwnerService} from "../../services/owner.service";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-edit-owner-modal',
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    MatCheckbox,
    MatDialogClose
  ],
  templateUrl: './edit-owner-modal.component.html',
  styleUrl: './edit-owner-modal.component.scss',
})
export class EditOwnerModalComponent {
  protected title = "Új tulajdonos létrehozása";
  protected ownerForm: UntypedFormGroup;

  constructor(@Inject(MAT_DIALOG_DATA) public data: OwnerUpdateDto,
              private ownerService: OwnerService,
              private dialogRef: MatDialogRef<EditOwnerModalComponent>,
              private snackBar: MatSnackBar,
              private fb: FormBuilder) {

    if (data.name !== "") {
      this.title = "Tulajdonos adatainak szerkesztése";
    }

    this.ownerForm = this.fb.group({
      name: ['', Validators.required],
      inSchInventory: [false]
    });
  }

  ngOnInit() {
    this.ownerForm.patchValue(this.data)
  }

  protected save() {
    if (!this.ownerForm.valid) {
      this.snackBar.open('Tölts ki minden kötelező mezőt!', "Let's do it!", {
        duration: 3000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
      });

      return;
    }

    const ownerToSave = new OwnerCreateDto(
      this.ownerForm.value.name,
      this.ownerForm.value.inSchInventory
    );

    this.dialogRef.close(ownerToSave);
  }
}
