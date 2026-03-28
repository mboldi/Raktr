import {Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {MainLayoutComponent} from './layouts/main-layout/main-layout.component';
import {autoLoginPartialRoutesGuard} from 'angular-auth-oidc-client';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  }, {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  }, {
    path: '',
    component: MainLayoutComponent,
    canActivate: [autoLoginPartialRoutesGuard]
  }
];
