import {ChangeDetectionStrategy, Component, Inject, ViewChild} from '@angular/core';
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
  @ViewChild(DeviceFormComponent) deviceFormComponent!: DeviceFormComponent;

  protected isNew: boolean = true;

  constructor(@Inject(MAT_DIALOG_DATA) protected deviceData: DeviceDetails,
              private dialogRef: MatDialogRef<DeviceEditDialogComponent>,
              private snackBar: MatSnackBar,
              private deviceService: DeviceService) {
    if(deviceData) {
      this.isNew = false;
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

        this.deviceService.updateDevice(this.deviceData.id, updateDevice).subscribe(updatedDevice => {
          this.dialogRef.close(updatedDevice);
        })
      }

    } else {
      this.snackBar.open('Tölts ki minden kötelező mezőt!', "Let's do it!", {
        duration: 3000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
      });
    }

  }
}
