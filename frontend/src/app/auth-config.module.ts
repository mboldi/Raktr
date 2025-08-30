import { NgModule } from '@angular/core';
import { AbstractSecurityStorage, AuthModule, LogLevel } from 'angular-auth-oidc-client';
import { AuthLocalStorageService } from './helpers/auth-local-storage.service';

@NgModule({
  imports: [
    AuthModule.forRoot({
      config: {
        authority: 'https://login.bsstudio.hu/application/o/raktr/',
        redirectUrl: window.location.origin,
        postLogoutRedirectUri: window.location.origin,
        clientId: '2wD6qaqGsYefuYv271cwjUYUDeL2HXJFhj2Omfbg',
        scope: 'openid profile offline_access',
        responseType: 'code',
        silentRenew: true,
        useRefreshToken: true,
        //logLevel: LogLevel.Debug,
        secureRoutes: ['http://localhost:8080'],
      },
    }),
  ],
  providers: [{ provide: AbstractSecurityStorage, useClass: AuthLocalStorageService }],
  exports: [AuthModule],
})
export class AuthConfigModule {}
