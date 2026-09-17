import {Component, Inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {
    MAT_DIALOG_DATA, MatDialog,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent, MatDialogRef,
    MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {FormControl, ReactiveFormsModule} from "@angular/forms";

export class OnlyNameDialogData {
    name: string;
    title: string;

    constructor(name: string, title: string) {
        this.name = name;
        this.title = title;
    }
}

@Component({
    selector: 'app-onlyname-edit-modal',
    imports: [
        MatButton,
        MatDialogActions,
        MatDialogContent,
        MatDialogTitle,
        MatFormField,
        MatInput,
        MatLabel,
        ReactiveFormsModule,
        MatDialogClose
    ],
    templateUrl: './onlyname-edit-modal.component.html',
    styleUrl: './onlyname-edit-modal.component.scss',
})
export class OnlynameEditModalComponent {
    protected editNameFormControl: FormControl = new FormControl();

    protected name = "";
    protected title = "";

    constructor(@Inject(MAT_DIALOG_DATA) protected data: OnlyNameDialogData,
                private dialogRef: MatDialogRef<OnlynameEditModalComponent>) {
        this.title = data.title;
        this.name = data.name;
    }

    protected save() {
        this.dialogRef.close(this.editNameFormControl.value);
    }
}
