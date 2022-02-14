import {Routes} from '@angular/router';
import {UserProfileComponent} from '../../user-profile/user-profile.component';
import {DevicesComponent} from '../../devices/devices.component';
import {OverviewComponent} from '../../overview/overview.component';
import {RentsComponent} from '../../rents/rents.component';
import {EditRentComponent} from '../../edit-rent/edit-rent.component';
import {AuthGuard} from '../../helpers/auth.guard';

export const AdminLayoutRoutes: Routes = [
    {path: 'overview', component: OverviewComponent, canActivate: [AuthGuard]},
    {path: 'devices', component: DevicesComponent, canActivate: [AuthGuard]},
    {path: 'rents', component: RentsComponent, canActivate: [AuthGuard]},
    {path: 'user-profile', component: UserProfileComponent, canActivate: [AuthGuard]},
    {path: 'rent/:id', component: EditRentComponent, canActivate: [AuthGuard]},
];
