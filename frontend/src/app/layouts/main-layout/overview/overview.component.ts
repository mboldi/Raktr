import { Component } from '@angular/core';
import {MatCard, MatCardContent, MatCardHeader} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {MatFabButton, MatIconButton} from '@angular/material/button';
import {FormControl, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-overview',
  imports: [
    MatCard,
    MatCardHeader,
    MatIcon,
    MatCardContent,
    MatFormField,
    MatLabel,
    MatInput,
    MatIconButton,
    MatSuffix,
    ReactiveFormsModule,
    MatFabButton
  ],
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.scss',
})
export class OverviewComponent {
  protected deviceSearchFormControl: FormControl = new FormControl();

  protected searchDevice() {
    console.log(this.deviceSearchFormControl.value);
    this.deviceSearchFormControl.setValue("");
  }

  protected addRent() {

  }
}
