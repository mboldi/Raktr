import {Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {MainLayoutComponent} from './layouts/main-layout/main-layout.component';
import {autoLoginPartialRoutesGuard} from 'angular-auth-oidc-client';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  }, {
    path: 'login',
    component: LoginComponent
  }, {
    path: '',
    component: MainLayoutComponent,
    children: [{
      path: '',
      loadChildren: () => import('./layouts/main-layout/main-layout.routes').then(m => m.MAIN_LAYOUT_ROUTES)
    }],
    canActivate: [autoLoginPartialRoutesGuard]
  }
];
