import {Component, Inject, Type} from '@angular/core';
import {NgComponentOutlet} from '@angular/common';
import {MatButton, MatFabButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatTab, MatTabGroup, MatTabLabel} from '@angular/material/tabs';
import {DeviceDetails} from '../../model/scannable/device/deviceDetails';
import {ScannableDetailsDto} from '../../model/scannable/scannableDetailsDto';
import {MatIcon} from '@angular/material/icon';
import {DeviceViewPageComponent} from '../device-view-page/device-view-page.component';
import {ScannableViewPageComponent} from '../scannable-view-page/scannable-view-page.component';

export type TabbedEditModalKind = 'device' | 'scannable';

export interface TabbedEditModalData {
  kind: TabbedEditModalKind;
  item: DeviceDetails | ScannableDetailsDto;
}

interface TabbedEditModalViewDefinition {
  component: Type<unknown>;
  icon: string;
  tabLabel: string;
  title: string;
  editable: boolean;
  toInputs: (item: DeviceDetails | ScannableDetailsDto) => Record<string, unknown>;
}

// Add an entry here to support opening a new *-view-page type in this modal - no template changes needed.
const VIEW_DEFINITIONS: Record<TabbedEditModalKind, TabbedEditModalViewDefinition> = {
  device: {
    component: DeviceViewPageComponent,
    icon: 'edit_note',
    tabLabel: 'Eszköz adatok',
    title: 'Eszköz adatai',
    editable: true,
    toInputs: item => ({device: item}),
  },
  scannable: {
    component: ScannableViewPageComponent,
    icon: 'edit_note',
    tabLabel: 'Tulajdonságok',
    title: 'Elem adatai',
    editable: false,
    toInputs: item => ({scannable: item}),
  },
};

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
    NgComponentOutlet
  ],
  templateUrl: './tabbed-edit-modal.component.html',
  styleUrl: './tabbed-edit-modal.component.scss',
})
export class TabbedEditModalComponent {
  protected readonly view: TabbedEditModalViewDefinition;
  protected readonly viewInputs: Record<string, unknown>;
  protected readonly title: string;

  constructor(
    @Inject(MAT_DIALOG_DATA) protected data: TabbedEditModalData,
    private dialogRef: MatDialogRef<TabbedEditModalComponent>,
  ) {
    this.view = VIEW_DEFINITIONS[data.kind];
    this.viewInputs = this.view.toInputs(data.item);
    this.title = this.view.title;
  }

  protected edit() {
    this.dialogRef.close('edit');
  }
}
