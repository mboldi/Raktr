import {Component, ViewChild} from '@angular/core';
import {DatePipe} from "@angular/common";
import {MatCard} from "@angular/material/card";
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell, MatHeaderCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow, MatRowDef, MatTable
} from "@angular/material/table";
import {MatFabButton, MatIconButton, MatMiniFabButton} from "@angular/material/button";
import {MatFormField, MatInput, MatLabel, MatSuffix} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {LocationService} from "../../../../services/location.service";
import {MatDialog} from "@angular/material/dialog";
import {MatSnackBar} from "@angular/material/snack-bar";
import {
    OnlyNameDialogData,
    OnlynameEditModalComponent
} from "../../../../components/onlyname-edit-modal/onlyname-edit-modal.component";
import {YesnoModalComponent} from "../../../../components/yesno-modal/yesno-modal.component";
import {LocationDetails} from "../../../../model/location/LocationDetails";

const COLUMNS: string[] = ['name', 'assignedScannables', 'createdAt', 'createdBy', 'delete'];

@Component({
    selector: 'app-locations',
    imports: [
        DatePipe,
        MatCard,
        MatCell,
        MatCellDef,
        MatColumnDef,
        MatFabButton,
        MatFormField,
        MatHeaderCell,
        MatHeaderRow,
        MatHeaderRowDef,
        MatIcon,
        MatIconButton,
        MatInput,
        MatLabel,
        MatMiniFabButton,
        MatProgressSpinner,
        MatRow,
        MatRowDef,
        MatSuffix,
        MatTable,
        ReactiveFormsModule,
        MatHeaderCellDef
    ],
    templateUrl: './locations.component.html',
    styleUrl: './locations.component.scss',
})
export class LocationsComponent {
    @ViewChild(MatTable) locationTable!: MatTable<any>;

    protected locationSearchFormControl = new FormControl();

    protected locations: LocationDetails[] = [];
    protected filteredLocations: LocationDetails[] = [];

    protected loading = true;
    protected columns = COLUMNS;

    constructor(private locationService: LocationService,
                private dialog: MatDialog,
                private snackBar: MatSnackBar,) {
    }

    ngOnInit(): void {
        this.getCategories();
    }

    private getCategories() {
        this.locationService.getLocations().subscribe((data) => {
            this.locations = data;
            this.loading = false;

            this.applyFilter();
        });
    }

    protected applyFilter() {
        const searchValue = this.locationSearchFormControl.value;

        if (searchValue && searchValue.length > 0) {
            this.filteredLocations = this.locations.filter(category => category.name.toLowerCase().includes(searchValue.toLowerCase()));
        } else {
            this.filteredLocations = this.locations;
        }

        this.filteredLocations = this.filteredLocations.sort((a, b) => a.name.localeCompare(b.name));

        this.locationTable.renderRows();
    }

    protected newLocation() {
        const editCategoryDialog = this.dialog.open(OnlynameEditModalComponent, {
            width: '20vw',
            minWidth: '350px',
            data: new OnlyNameDialogData('', 'Új tárolási hely')
        });

        editCategoryDialog.afterClosed().subscribe(result => {
            if (result) {
                this.locationService.addLocation(result).subscribe((data) => {
                    this.locations.push(data);
                    this.applyFilter();

                    this.snackBar.open(`${data.name} tárolási hely létrehozva!`, "Remek!", {
                        duration: 3000,
                        horizontalPosition: 'right',
                        verticalPosition: 'top',
                    });
                })
            }
        })
    }

    protected deleteLocation(name: string) {
        const yesnoDialog = this.dialog.open(YesnoModalComponent, {
            width: '20vw',
            minWidth: '350px',
            data: `Biztos törölnéd a(z) ${name} tárolási helyet?`
        });

        yesnoDialog.afterClosed().subscribe(result => {
            if (result) {
                this.locationService.deleteLocation(name).subscribe((data) => {
                    this.getCategories();

                    this.snackBar.open(`${name} kategória törölve!`, ":(", {
                        duration: 3000,
                        horizontalPosition: 'right',
                        verticalPosition: 'top',
                    });
                })
            }
        })
    }
}
