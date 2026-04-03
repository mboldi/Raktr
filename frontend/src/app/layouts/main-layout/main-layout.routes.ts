import {Routes} from '@angular/router';
import {OverviewComponent} from './overview/overview.component';
import {InventoryComponent} from './inventory/inventory.component';
import {TicketsComponent} from './tickets/tickets.component';
import {SettingsComponent} from './settings/settings.component';
import { RentsComponent } from "./rents/rents.component";

export const MAIN_LAYOUT_ROUTES: Routes = [
  {path: '', redirectTo: 'overview', pathMatch: 'full'},
  {path: 'overview', component: OverviewComponent},
  {
    path: 'inventory',
    component: InventoryComponent,
    loadChildren: () => import('./inventory/inventory.routes').then(m => m.INVENTORY_ROUTES)
  },
  {path: 'rents', component: RentsComponent},
  {path: 'tickets', component: TicketsComponent},
  {path: 'settings', component: SettingsComponent},
];
