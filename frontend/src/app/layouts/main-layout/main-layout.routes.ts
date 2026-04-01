// main-layout.routes.ts
import { Routes } from '@angular/router';
import {OverviewComponent} from './overview/overview.component';

export const MAIN_LAYOUT_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'overview',
    pathMatch: 'full'
  },
  {
    path: 'overview',
    component: OverviewComponent
  }
];
