import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  private readonly oidcSecurityService = inject(OidcSecurityService);

  constructor(private router: Router, private title: Title) {
    title.setTitle('Raktr - bejelentkezés');
  }

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({ isAuthenticated }) => {
        if (isAuthenticated) {
          this.router.navigateByUrl('/overview');
        }
      }
    );
  }

  login() {
    this.oidcSecurityService.authorize();
  }
}
