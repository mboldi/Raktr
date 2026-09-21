import { Component, ChangeDetectionStrategy, inject, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { Router } from '@angular/router';
import { MatButton } from '@angular/material/button';
import { MatCard, MatCardContent, MatCardHeader } from '@angular/material/card';

@Component({
  selector: 'app-login',
  imports: [MatButton, MatCard, MatCardHeader, MatCardContent],
  templateUrl: './login.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './login.component.scss',
})
export class LoginComponent implements OnInit {
  private router = inject(Router);
  private titleService = inject(Title);
  private oidcSecurityService = inject(OidcSecurityService);

  constructor() {
    this.titleService.setTitle('Raktr - Bejelentkezés');
  }

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(({ isAuthenticated }) => {
      if (isAuthenticated) {
        this.router.navigateByUrl('/overview');
      }
    });
  }

  login(): void {
    this.oidcSecurityService.authorize();
  }
}
