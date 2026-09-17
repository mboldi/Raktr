import {Component, Input} from '@angular/core';
import {ContainerDetails} from '../../model/scannable/container/containerDetails';
import {MatIcon} from '@angular/material/icon';
import {MatChip} from '@angular/material/chips';
import {MatDivider} from '@angular/material/list';
import {MatCard, MatCardContent} from '@angular/material/card';
import {DecimalPipe} from '@angular/common';

@Component({
  selector: 'app-container-view-page',
  imports: [
    MatIcon,
    MatChip,
    MatDivider,
    MatCardContent,
    MatCard,
    DecimalPipe
  ],
  templateUrl: './container-view-page.component.html',
  styleUrl: './container-view-page.component.scss',
})
export class ContainerViewPageComponent {
  @Input({required: true}) container!: ContainerDetails;
}
