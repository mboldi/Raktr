import {Component} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {OidcSecurityService} from 'angular-auth-oidc-client';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'Raktr';

  constructor(private oidcSecurityService: OidcSecurityService) {}

  ngOnInit() {
    this.oidcSecurityService.checkAuth().subscribe(({ isAuthenticated, userData }) => {
      localStorage.setItem('username', userData['preferred_username']);
      //console.log('isAuthenticated', isAuthenticated);
      //console.log('userData', userData);
    });
  }
}
