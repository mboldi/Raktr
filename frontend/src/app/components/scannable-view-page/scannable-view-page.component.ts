import {Component, Input, ChangeDetectionStrategy} from '@angular/core';
import {ScannableDetailsDto} from '../../model/scannable/scannableDetailsDto';
import {MatIcon} from '@angular/material/icon';
import {MatChip} from '@angular/material/chips';
import {MatDivider} from '@angular/material/list';
import {MatCard, MatCardContent} from '@angular/material/card';
import {DecimalPipe} from '@angular/common';

@Component({
  selector: 'app-scannable-view-page',
  imports: [
    MatIcon,
    MatChip,
    MatDivider,
    MatCardContent,
    MatCard,
    DecimalPipe
  ],
  templateUrl: './scannable-view-page.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './scannable-view-page.component.scss',
})
export class ScannableViewPageComponent {
  @Input({required: true}) scannable!: ScannableDetailsDto;
}
