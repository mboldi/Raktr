import { Component } from '@angular/core';
import {OidcSecurityService} from 'angular-auth-oidc-client';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-main-layout',
  imports: [
    MatButton
  ],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss'
})
export class MainLayoutComponent {

  constructor(private oidcSecurityService: OidcSecurityService) {
  }

  protected logout() {
    this.oidcSecurityService.logoff().subscribe();
  }
}
