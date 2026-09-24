import { ApplicationConfig, isDevMode, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import {
  AbstractSecurityStorage,
  authInterceptor,
  LogLevel,
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
    { provide: AbstractSecurityStorage, useClass: LocalStorageService },
    ...provideSentry(),
    provideNativeDateAdapter(),
    // Dialogs opened without their own maxHeight would otherwise grow past the viewport with
    // no way to scroll to the rest - this bounds every dialog so it can always be scrolled.
    { provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: { maxHeight: '90vh' } },
  ],
};
