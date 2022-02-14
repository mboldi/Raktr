import {Routes} from '@angular/router';
import {UserProfileComponent} from '../../user-profile/user-profile.component';
import {DevicesComponent} from '../../devices/devices.component';
import {OverviewComponent} from '../../overview/overview.component';
import {RentsComponent} from '../../rents/rents.component';
import {EditRentComponent} from '../../edit-rent/edit-rent.component';
import {AuthGuard} from '../../helpers/auth.guard';
import {ProjectsComponent} from '../../projects/projects.component';
import {TicketsComponent} from '../../tickets/tickets.component';
import {EditProjectComponent} from '../../edit-project/edit-project.component';

export const AdminLayoutRoutes: Routes = [
    {path: 'overview', component: OverviewComponent, canActivate: [AuthGuard]},
    {path: 'devices', component: DevicesComponent, canActivate: [AuthGuard]},
    {path: 'rents', component: RentsComponent, canActivate: [AuthGuard]},
    {path: 'tickets', component: TicketsComponent, canActivate: [AuthGuard]},
    {path: 'projects', component: ProjectsComponent, canActivate: [AuthGuard]},
    {path: 'edit-project/:id', component: EditProjectComponent, canActivate: [AuthGuard]},
    {path: 'user-profile', component: UserProfileComponent, canActivate: [AuthGuard]},
    {path: 'rent/:id', component: EditRentComponent, canActivate: [AuthGuard]},
];
