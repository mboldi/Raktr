import {Routes} from '@angular/router';
import {CompositeItemsComponent} from './composite-items/composite-items.component';
import {DevicesComponent} from './devices/devices.component';
import {ContainersComponent} from './containers/containers.component';


export const INVENTORY_ROUTES: Routes = [
  {path: '', redirectTo: 'devices', pathMatch: 'full'},
  {path: 'devices', component: DevicesComponent},
  {path: 'compositeitems', component: CompositeItemsComponent},
  {path: 'containers', component: ContainersComponent},
];
