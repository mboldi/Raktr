import { ChangeDetectionStrategy, Component, ViewChild, inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent, MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';
import {DeviceDetails} from '../../model/scannable/device/deviceDetails';
import {DeviceFormComponent} from '../device-form/device-form.component';
import {DeviceCreateDto} from '../../model/scannable/device/deviceCreateDto';
import {MatSnackBar} from '@angular/material/snack-bar';
import {DeviceService} from '../../services/device.service';
import {DeviceUpdateDto} from '../../model/scannable/device/deviceUpdateDto';

export interface DeviceDialogData {
  /** Pass to open the dialog in edit mode for an existing device. */
  device?: DeviceDetails;
  /** Pass when creating a new device from a barcode the user already searched for, so it's
   * locked in instead of auto-generating a fresh one. */
  presetBarcode?: string;
}

@Component({
  selector: 'app-device-edit-modal',
  imports: [
    MatDialogClose,
    MatButton,
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle,
    DeviceFormComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './device-edit-dialog.component.html',
  styleUrl: './device-edit-dialog.component.scss',
})
export class DeviceEditDialogComponent {
  protected dialogData = inject<DeviceDialogData | undefined>(MAT_DIALOG_DATA);
  private dialogRef = inject<MatDialogRef<DeviceEditDialogComponent>>(MatDialogRef);
  private snackBar = inject(MatSnackBar);
  private deviceService = inject(DeviceService);

  @ViewChild(DeviceFormComponent) deviceFormComponent!: DeviceFormComponent;

  protected title = 'Új eszköz hozzáadása';
  protected isNew = true;
  protected deviceData: DeviceDetails | null;

  constructor() {
    const dialogData = this.dialogData;

    this.deviceData = dialogData?.device ?? null;

    if (this.deviceData) {
      this.isNew = false;
      this.title = 'Eszköz szerkesztése';
    }
  }

  protected get isFormValid(): boolean {
    return this.deviceFormComponent?.deviceForm?.valid ?? false;
  }

  protected getFormValue(): Partial<DeviceDetails> {
    return this.deviceFormComponent?.deviceForm?.value ?? {};
  }

  protected save() {
    if (this.isFormValid) {     // New device, has to be created
      if(this.isNew){
        const newDevice = DeviceCreateDto.fromFormControl(this.deviceFormComponent?.deviceForm?.value);

        this.deviceService.createDevice(newDevice).subscribe(createdDevice => {
          this.dialogRef.close(createdDevice);
        });
      } else {                  // Device exists, only update
        const updateDevice = DeviceUpdateDto.fromFormControl(this.deviceFormComponent?.deviceForm?.value);

        this.deviceService.updateDevice(this.deviceData!.id, updateDevice).subscribe(updatedDevice => {
          this.dialogRef.close(updatedDevice);
        })
      }

    } else {
      this.deviceFormComponent?.markAllFieldsAsTouched();

      this.snackBar.open('Tölts ki minden kötelező mezőt!', "Let's do it!", {
        duration: 3000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['error-snackbar'],
      });
    }

  }
}
