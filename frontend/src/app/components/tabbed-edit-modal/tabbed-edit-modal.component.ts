import {Component, Inject} from '@angular/core';
import {MatButton, MatFabButton, MatIconButton, MatMiniFabButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatTab, MatTabGroup, MatTabLabel} from '@angular/material/tabs';
import {DeviceFormComponent} from '../device-form/device-form.component';
import {DeviceDetails} from '../../model/scannable/device/deviceDetails';
import {MatIcon} from '@angular/material/icon';
import {DeviceViewPageComponent} from '../device-view-page/device-view-page.component';

@Component({
  selector: 'app-tabbed-edit-modal',
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogTitle,
    MatDialogClose,
    MatTabGroup,
    MatTab,
    MatTabLabel,
    MatIcon,
    MatFabButton,
    DeviceViewPageComponent
  ],
  templateUrl: './tabbed-edit-modal.component.html',
  styleUrl: './tabbed-edit-modal.component.scss',
})
export class TabbedEditModalComponent {
  protected isNew = false;
  protected title = 'Eszköz szerkesztése';

  constructor(
    @Inject(MAT_DIALOG_DATA) protected deviceData: DeviceDetails,
    private dialogRef: MatDialogRef<TabbedEditModalComponent>,
  ) {
    if (deviceData) {
      this.isNew = false;
      this.title = 'Eszköz szerkesztése';
    }
  }


  protected editDevice() {
    this.dialogRef.close('edit');
  }
}
