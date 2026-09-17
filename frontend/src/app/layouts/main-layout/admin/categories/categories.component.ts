import {Component, ViewChild} from '@angular/core';
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCard} from "@angular/material/card";
import {MatFabButton, MatIconButton, MatMiniFabButton} from "@angular/material/button";
import {MatFormField, MatInput, MatLabel, MatSuffix} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {CategoryDetails} from '../../../../model/category/categoryDetails';
import {CategoryService} from '../../../../services/category.service';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell, MatHeaderCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {DatePipe} from '@angular/common';
import {
    OnlyNameDialogData,
    OnlynameEditModalComponent
} from "../../../../components/onlyname-edit-modal/onlyname-edit-modal.component";
import {MatDialog} from "@angular/material/dialog";
import {YesnoModalComponent} from "../../../../components/yesno-modal/yesno-modal.component";
import {MatSnackBar} from "@angular/material/snack-bar";
import {HttpErrorResponse} from '@angular/common/http';


const COLUMNS: string[] = ['name', 'assignedScannables', 'createdAt', 'createdBy', 'delete'];

@Component({
    selector: 'app-categories',
    imports: [
        FormsModule,
        MatCard,
        MatFabButton,
        MatFormField,
        MatIcon,
        MatIconButton,
        MatInput,
        MatLabel,
        MatSuffix,
        ReactiveFormsModule,
        MatCell,
        MatCellDef,
        MatColumnDef,
        MatHeaderCell,
        MatHeaderRow,
        MatHeaderRowDef,
        MatProgressSpinner,
        MatRow,
        MatRowDef,
        MatTable,
        MatHeaderCellDef,
        DatePipe,
        MatMiniFabButton
    ],
    templateUrl: './categories.component.html',
    styleUrl: './categories.component.scss',
})
export class CategoriesComponent {
    @ViewChild(MatTable) categoryTable!: MatTable<any>;

    protected categorySearchFormControl = new FormControl();

    protected categories: CategoryDetails[] = [];
    protected filteredCategories: CategoryDetails[] = [];

    protected loading = true;
    protected columns = COLUMNS;

    constructor(private categoryService: CategoryService,
                private dialog: MatDialog,
                private snackBar: MatSnackBar,) {

    }

    ngOnInit(): void {
        this.getCategories();
    }

    private getCategories() {
        this.categoryService.getCategories().subscribe((data) => {
            this.categories = data;
            this.loading = false;

            this.applyFilter();
        });
    }

    protected applyFilter() {
        const searchValue = this.categorySearchFormControl.value;

        if (searchValue && searchValue.length > 0) {
            this.filteredCategories = this.categories.filter(category => category.name.toLowerCase().includes(searchValue.toLowerCase()));
        } else {
            this.filteredCategories = this.categories;
        }

        this.filteredCategories = this.filteredCategories.sort((a, b) => a.name.localeCompare(b.name));

        this.categoryTable.renderRows();
    }

    protected newCategory() {
        const editCategoryDialog = this.dialog.open(OnlynameEditModalComponent, {
            width: '20vw',
            minWidth: '350px',
            data: new OnlyNameDialogData('', 'Új Kategória')
        });

        editCategoryDialog.afterClosed().subscribe(result => {
            if (result) {
                this.categoryService.addCategory(result).subscribe({
                    next: (data) => {
                        this.categories.push(data);
                        this.applyFilter();

                        this.snackBar.open(`${data.name} kategória létrehozva!`, "Remek!", {
                            duration: 3000,
                            horizontalPosition: 'right',
                            verticalPosition: 'top',
                            panelClass: ['success-snackbar'],
                        });
                    },
                    error: () => {
                        this.snackBar.open(`Nem sikerült létrehozni a(z) ${result} kategóriát! :(`, "Értem", {
                            duration: 4000,
                            horizontalPosition: 'right',
                            verticalPosition: 'top',
                            panelClass: ['error-snackbar'],
                        });
                    }
                })
            }
        })
    }

    protected deleteCategory(name: string) {
        const yesnoDialog = this.dialog.open(YesnoModalComponent, {
            width: '20vw',
            minWidth: '350px',
            data: `Biztos törölnéd a(z) ${name} kategóriát?`
        });

        yesnoDialog.afterClosed().subscribe(result => {
            if (result) {
                this.categoryService.deleteCategory(name).subscribe({
                    next: () => {
                        this.getCategories();

                        this.snackBar.open(`${name} kategória törölve!`, "Remek!", {
                            duration: 3000,
                            horizontalPosition: 'right',
                            verticalPosition: 'top',
                            panelClass: ['success-snackbar'],
                        });
                    },
                    error: (error: HttpErrorResponse) => {
                        if (error.status === 409) {
                            this.snackBar.open(`A(z) ${name} kategória használatban van, nem törölhető!`, "Hoppá!", {
                                duration: 4000,
                                horizontalPosition: 'right',
                                verticalPosition: 'top',
                                panelClass: ['error-snackbar'],
                            });
                        } else {
                            this.snackBar.open(`Nem sikerült törölni a(z) ${name} kategóriát!`, "Borzalom", {
                                duration: 4000,
                                horizontalPosition: 'right',
                                verticalPosition: 'top',
                                panelClass: ['error-snackbar'],
                            });
                        }
                    }
                })
            }
        })
    }
}
