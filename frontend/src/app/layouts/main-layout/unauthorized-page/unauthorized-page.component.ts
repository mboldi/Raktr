import {Component, ChangeDetectionStrategy} from '@angular/core';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-unauthorized-page',
  imports: [
    MatIcon
  ],
  templateUrl: './unauthorized-page.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './unauthorized-page.component.scss',
})
export class UnauthorizedPageComponent {
}
