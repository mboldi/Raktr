import {
  ApplicationConfig,
  inject,
  isDevMode,
  provideAppInitializer,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { catchError } from 'rxjs';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import {
  AbstractSecurityStorage,
  authInterceptor,
  LogLevel,
  OidcSecurityService,
  provideAuth,
} from 'angular-auth-oidc-client';
import { LocalStorageService } from './services/localStorage.service';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MAT_DIALOG_DEFAULT_OPTIONS } from '@angular/material/dialog';
import { provideSentry } from './sentry';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor()])),
    provideAuth({
      config: {
        authority: 'https://login.bsstudio.hu/application/o/raktr',
        redirectUrl: window.location.origin,
        postLogoutRedirectUri: window.location.origin + '/overview',
        clientId: '2wD6qaqGsYefuYv271cwjUYUDeL2HXJFhj2Omfbg',
        scope: 'openid profile offline_access',
        responseType: 'code',
        silentRenew: true,
        useRefreshToken: true,
        logLevel: isDevMode() ? LogLevel.Debug : LogLevel.Warn,
        secureRoutes: ['/api'],
      },
    }),
    // The initializer finishes the login check (including refreshing an expired token) before
    // routing starts, so the route guard never sees a half-refreshed session and re-logs in.
    provideAppInitializer(checkAuth),
    { provide: AbstractSecurityStorage, useClass: LocalStorageService },
    ...provideSentry(),
    provideNativeDateAdapter(),
    // Dialogs opened without their own maxHeight would otherwise grow past the viewport with
    // no way to scroll to the rest - this bounds every dialog so it can always be scrolled.
    { provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: { maxHeight: '90vh' } },
  ],
};

/**
 * The login check throws on a callback URL whose state this browser no longer holds, such as
 * one from an older login in another tab. Without that URL's query it finds the stored session,
 * if there is one.
 */
function checkAuth() {
  const oidcSecurityService = inject(OidcSecurityService);

  return oidcSecurityService.checkAuthMultiple().pipe(
    catchError(() => {
      window.history.replaceState(null, '', window.location.pathname);
      return oidcSecurityService.checkAuthMultiple();
    }),
  );
}
