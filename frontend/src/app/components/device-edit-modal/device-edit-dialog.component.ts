import {Component, OnInit} from '@angular/core';
import {MatDialogActions, MatDialogClose, MatDialogContent, MatDialogTitle} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';
import {OwnerService} from '../../services/owner.service';
import {Owner} from '../../model/owner/owner';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatCheckbox} from '@angular/material/checkbox';

@Component({
  selector: 'app-device-edit-modal',
  imports: [
    MatDialogClose,
    MatButton,
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    MatLabel,
    MatInput,
    MatCheckbox
  ],
  templateUrl: './device-edit-dialog.component.html',
  styleUrl: './device-edit-dialog.component.scss',
})
export class DeviceEditDialogComponent implements OnInit {
  protected owners: Owner[] = [];
  protected categories: string[] = [];

  constructor(private ownerService: OwnerService) {
  }

  ngOnInit() {
    this.ownerService.getOwners().subscribe(owners => {
      this.owners = owners;
    });
  }

}
