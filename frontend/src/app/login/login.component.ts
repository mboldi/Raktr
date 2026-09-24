import { Component, ChangeDetectionStrategy, inject, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { Router } from '@angular/router';
import { MatButton } from '@angular/material/button';
import { MatCard, MatCardContent, MatCardHeader } from '@angular/material/card';
import { LocalStorageService } from '../services/localStorage.service';

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
  private localStorageService = inject(LocalStorageService);

  constructor() {
    this.titleService.setTitle('Raktr - Bejelentkezés');
  }

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(({ isAuthenticated }) => {
      if (isAuthenticated) {
        const returnUrl = this.localStorageService.read('returnUrl');
        this.localStorageService.remove('returnUrl');
        this.router.navigateByUrl(returnUrl || '/overview');
      }
    });
  }

  login(): void {
    this.oidcSecurityService.authorize();
  }
}
