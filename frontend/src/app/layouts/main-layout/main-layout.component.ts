import { Component } from '@angular/core';
import {OidcSecurityService} from 'angular-auth-oidc-client';
import {SidebarComponent} from '../../components/sidebar/sidebar.component';

@Component({
  selector: 'app-main-layout',
  imports: [
    SidebarComponent
  ],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent {

  constructor() {
  }

}
