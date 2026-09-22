import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ViewChild,
  inject,
} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatButton } from '@angular/material/button';
import { ContainerDetails } from '../../model/scannable/container/containerDetails';
import { AddDeviceEvent, ContainerFormComponent } from '../container-form/container-form.component';
import { ContainerCreateDto } from '../../model/scannable/container/containerCreateDto';
import { ContainerUpdateDto } from '../../model/scannable/container/containerUpdateDto';
import { ContainerAddDevicesDto } from '../../model/scannable/container/containerAddDevicesDto';
import { ContainerAddItemDto } from '../../model/scannable/container/containerAddItemDto';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ContainerService } from '../../services/container.service';
import { DeviceDetails } from '../../model/scannable/device/deviceDetails';
import { HttpErrorResponse } from '@angular/common/http';
import { filter } from 'rxjs';

export interface ContainerDialogData {
  /** Pass to open the dialog in edit mode for an existing container. */
  container?: ContainerDetails;
  /** Pass to open the dialog in create mode, pre-filled with another container's values
   * (except its barcode, which is freshly generated). */
  duplicateFrom?: ContainerDetails;
}

@Component({
  selector: 'app-container-edit-modal',
  imports: [MatButton, MatDialogActions, MatDialogContent, MatDialogTitle, ContainerFormComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './container-edit-dialog.component.html',
  styleUrl: './container-edit-dialog.component.scss',
})
export class ContainerEditDialogComponent {
  private dialogRef = inject<MatDialogRef<ContainerEditDialogComponent>>(MatDialogRef);
  private snackBar = inject(MatSnackBar);
  private containerService = inject(ContainerService);
  private cdr = inject(ChangeDetectorRef);

  @ViewChild(ContainerFormComponent) containerFormComponent!: ContainerFormComponent;

  protected isNew = true;
  protected containerData: ContainerDetails | null;
  protected duplicateFrom: ContainerDetails | null;
  protected containerName: string | null = null;

  constructor() {
    const dialogData = inject<ContainerDialogData | undefined>(MAT_DIALOG_DATA);

    this.containerData = dialogData?.container ?? null;
    this.duplicateFrom = dialogData?.duplicateFrom ?? null;

    if (this.containerData) {
      this.isNew = false;
      this.containerName = this.containerData.name;
    }

    // Report the latest item state (already saved via its own API calls) even when the
    // dialog is dismissed by clicking outside it or pressing Escape, not just "Vissza".
    this.dialogRef.disableClose = true;
    this.dialogRef.backdropClick().subscribe(() => this.close());
    this.dialogRef
      .keydownEvents()
      .pipe(filter((event) => event.key === 'Escape'))
      .subscribe(() => this.close());
  }

  protected get title(): string {
    if (this.isNew) {
      return this.duplicateFrom ? 'Szállítóláda duplikálása' : 'Új szállítóláda hozzáadása';
    }

    return this.containerName
      ? `Szállítóláda szerkesztése - ${this.containerName}`
      : 'Szállítóláda szerkesztése';
  }

  protected get isFormValid(): boolean {
    return this.containerFormComponent?.containerForm?.valid ?? false;
  }

  protected onFormChanged(value: Partial<ContainerDetails>) {
    if (!this.isNew) {
      this.containerName = value.name ?? this.containerName;
    }
  }

  protected close() {
    // Items added/removed while the dialog was open are already saved via their own API
    // calls, so closing without pressing "Mentés" should still report the latest item state.
    this.dialogRef.close(this.containerData);
  }

  protected save() {
    if (!this.isFormValid) {
      this.snackBar.open('Tölts ki minden kötelező mezőt!', "Let's do it!", {
        duration: 3000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['error-snackbar'],
      });
      return;
    }

    const formValue = this.containerFormComponent.containerForm.value;

    if (this.isNew) {
      const newContainer = new ContainerCreateDto(
        formValue.assetTag,
        formValue.barcode,
        formValue.name,
        formValue.weight,
        formValue.publicRentable,
        formValue.category,
        formValue.location,
        formValue.owner.id,
      );

      this.containerService.createContainer(newContainer).subscribe((createdContainer) => {
        this.dialogRef.close(createdContainer);
      });
    } else {
      const updateContainer = new ContainerUpdateDto(
        formValue.assetTag,
        formValue.barcode,
        formValue.name,
        formValue.weight,
        formValue.publicRentable,
        formValue.category,
        formValue.location,
        formValue.owner.id,
      );

      this.containerService
        .updateContainer(this.containerData!.id, updateContainer)
        .subscribe((updatedContainer) => {
          this.snackBar.open(`${updatedContainer.name} mentve!`, 'Kitűnő!', {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['success-snackbar'],
          });

          this.dialogRef.close(updatedContainer);
        });
    }
  }

  protected onAddDevice({ device, quantity }: AddDeviceEvent) {
    if (!this.containerData) {
      return;
    }

    this.containerService
      .addDevicesToContainer(
        this.containerData.id,
        new ContainerAddDevicesDto([new ContainerAddItemDto(device.id, quantity)]),
      )
      .subscribe({
        next: (updatedContainer) => {
          this.containerData = updatedContainer;
          this.cdr.markForCheck();

          this.snackBar.open(`${device.name} hozzáadva a szállítóládához!`, 'Remek!', {
            duration: 3000,
            horizontalPosition: 'right',
            verticalPosition: 'top',
            panelClass: ['success-snackbar'],
          });
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 409) {
            this.snackBar.open(
              `A(z) ${device.name} eszköz már hozzá van adva a szállítóládához!`,
              'Értem',
              {
                duration: 4000,
                horizontalPosition: 'right',
                verticalPosition: 'top',
                panelClass: ['error-snackbar'],
              },
            );
          } else {
            this.snackBar.open(`Nem sikerült hozzáadni a(z) ${device.name} eszközt!`, 'Értem', {
              duration: 4000,
              horizontalPosition: 'right',
              verticalPosition: 'top',
              panelClass: ['error-snackbar'],
            });
          }
        },
      });
  }

  protected onDeviceNotFound() {
    this.snackBar.open('Nem található eszköz ezzel a vonalkóddal!', 'Értem', {
      duration: 3000,
      horizontalPosition: 'right',
      verticalPosition: 'top',
      panelClass: ['error-snackbar'],
    });
  }

  protected onRemoveDevice(device: DeviceDetails) {
    if (!this.containerData) {
      return;
    }

    this.containerService
      .removeDeviceFromContainer(this.containerData.id, device.id)
      .subscribe((updatedContainer) => {
        this.containerData = updatedContainer;
        this.cdr.markForCheck();

        this.snackBar.open(`${device.name} eltávolítva a szállítóládából!`, 'Rendben', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['success-snackbar'],
        });
      });
  }
}
