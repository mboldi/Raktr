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
    path: 'app',
    component: MainLayoutComponent,
    canActivate: [autoLoginPartialRoutesGuard]
  }
];
