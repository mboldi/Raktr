import { ChangeDetectionStrategy, Component, Inject, ViewChild } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatButton } from '@angular/material/button';
import { DeviceDetails } from '../../model/scannable/device/DeviceDetails';
import { DeviceFormComponent } from '../device-form/device-form.component';

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

  constructor(@Inject(MAT_DIALOG_DATA) protected deviceData: DeviceDetails) {}

  protected get isFormValid(): boolean {
    return this.deviceFormComponent?.deviceForm?.valid ?? false;
  }

  protected getFormValue(): Partial<DeviceDetails> {
    return this.deviceFormComponent?.deviceForm?.value ?? {};
  }
}
