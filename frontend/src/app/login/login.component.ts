import { Component, ChangeDetectionStrategy, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { filter, take } from 'rxjs';
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
  private destroyRef = inject(DestroyRef);

  constructor() {
    this.titleService.setTitle('Raktr - Bejelentkezés');
  }

  ngOnInit(): void {
    // isAuthenticated$ emits again on every silent token refresh (about every 5 minutes), so this
    // must only react once and stop with the component - otherwise it stays subscribed after the
    // user has left the login page and throws them back to the overview on every refresh.
    this.oidcSecurityService.isAuthenticated$
      .pipe(
        filter(({ isAuthenticated }) => isAuthenticated),
        take(1),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(() => {
        const returnUrl = this.localStorageService.read('returnUrl');
        this.localStorageService.remove('returnUrl');
        this.router.navigateByUrl(returnUrl || '/overview');
      });
  }

  login(): void {
    this.oidcSecurityService.authorize();
  }
}
