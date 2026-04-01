import {ApplicationConfig, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {AbstractSecurityStorage, authInterceptor, LogLevel, provideAuth} from 'angular-auth-oidc-client';
import {LocalStorageService} from './services/localStorage.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({eventCoalescing: true}),
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
        logLevel: LogLevel.Debug,
        secureRoutes: ['http://localhost:8080']
      }
    }),
    { provide: AbstractSecurityStorage, useClass: LocalStorageService }
  ]
};
