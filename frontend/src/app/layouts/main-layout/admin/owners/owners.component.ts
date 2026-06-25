import {Component, ViewChild} from '@angular/core';
import {DatePipe} from "@angular/common";
import {MatCard} from "@angular/material/card";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatFabButton, MatIconButton, MatMiniFabButton} from "@angular/material/button";
import {MatFormField, MatInput, MatLabel, MatSuffix} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {OwnerDetailsDto} from "../../../../model/owner/ownerDetailsDto";
import {OwnerService} from "../../../../services/owner.service";
import {MatDialog} from "@angular/material/dialog";
import {MatSnackBar} from "@angular/material/snack-bar";
import {YesnoModalComponent} from "../../../../components/yesno-modal/yesno-modal.component";
import {OwnerCreateDto} from "../../../../model/owner/ownerCreateDto";
import {EditOwnerModalComponent} from "../../../../components/edit-owner-modal/edit-owner-modal.component";
import {OwnerUpdateDto} from "../../../../model/owner/ownerUpdateDto";

const COLUMNS: string[] = ['name', 'inSchInventory', 'assignedScannables', 'updatedAt', 'updatedBy', 'delete'];

@Component({
  selector: 'app-owners',
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
  templateUrl: './owners.component.html',
  styleUrl: './owners.component.scss',
})
export class OwnersComponent {
  @ViewChild(MatTable) ownerTable!: MatTable<any>;

  protected searchFormControl = new FormControl();

  protected loading = true;
  protected columns = COLUMNS;

  protected owners: OwnerDetailsDto[] = []
  protected filteredOwners: OwnerDetailsDto[] = []

  constructor(private ownerService: OwnerService,
              private dialog: MatDialog,
              private snackBar: MatSnackBar,) {
  }


  ngOnInit(): void {
    this.getCategories();
  }

  private getCategories() {
    this.ownerService.getOwners().subscribe((data) => {
      this.owners = data;
      this.loading = false;

      this.applyFilter();
    });
  }

  protected applyFilter() {
    const searchValue = this.searchFormControl.value;

    if (searchValue && searchValue.length > 0) {
      this.filteredOwners = this.owners.filter(category => category.name.toLowerCase().includes(searchValue.toLowerCase()));
    } else {
      this.filteredOwners = this.owners;
    }

    this.filteredOwners = this.filteredOwners.sort((a, b) => a.name.localeCompare(b.name));

    this.ownerTable.renderRows();
  }

  protected newOwner() {
    const editOwnerDialog = this.dialog.open(EditOwnerModalComponent, {
      width: '30vw',
      minWidth: '350px',
      data: new OwnerUpdateDto('', false)
    });

    editOwnerDialog.afterClosed().subscribe(result => {
      if (result !== false) {
        this.ownerService.addOwner(result).subscribe((data) => {
          this.owners.push(data);
          this.applyFilter();

          this.snackBar.open(`${data.name} tulajdonos létrehozva!`, "Remek!", {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
          });
        })
      }
    })
  }

  protected editOwner(ownerToUpdate: OwnerDetailsDto) {
    const editOwnerDialog = this.dialog.open(EditOwnerModalComponent, {
      width: '30vw',
      minWidth: '350px',
      data: ownerToUpdate
    });

    editOwnerDialog.afterClosed().subscribe(result => {
      if (result !== false && result !== undefined) {
        this.ownerService.updateOwner(ownerToUpdate.id, result).subscribe((data) => {
          this.getCategories();
          this.applyFilter();

          this.snackBar.open(`${data.name} tulajdonos szerkesztve!`, "Remek!", {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
          });
        })
      }
    })
  }

  protected deleteOwner(ownerToDelete: OwnerDetailsDto) {
    const yesnoDialog = this.dialog.open(YesnoModalComponent, {
      width: '20vw',
      minWidth: '350px',
      data: `Biztos törölnéd a(z) ${ownerToDelete.name} tulajdonost?`
    });

    yesnoDialog.afterClosed().subscribe(result => {
      if (result) {
        this.ownerService.deleteOwner(ownerToDelete.id).subscribe((deleted) => {
          this.getCategories();

          if (deleted) {
            this.snackBar.open(`${ownerToDelete.name} törölve!`, "Bye!", {
              duration: 3000,
              horizontalPosition: 'right',
              verticalPosition: 'top',
            });
          } else {
            this.snackBar.open(`Nem sikerült törölni ${ownerToDelete.name}-t!`, ":(", {
              duration: 3000,
              horizontalPosition: 'right',
              verticalPosition: 'top',
            });
          }
        })
      }
    })
  }
}
