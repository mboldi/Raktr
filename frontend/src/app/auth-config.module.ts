import { NgModule } from '@angular/core';
import { AbstractSecurityStorage, AuthModule, LogLevel } from 'angular-auth-oidc-client';
import { AuthLocalStorageService } from './helpers/auth-local-storage.service';

@NgModule({
  imports: [
    AuthModule.forRoot({
      config: {
        authority: 'https://login.bsstudio.hu/application/o/website-test',
        redirectUrl: window.location.origin,
        postLogoutRedirectUri: window.location.origin,
        clientId: 'H2w2dOKxioUPmjghbPNbXMcEiGxqT7A82mLO811d',
        scope: 'openid profile email offline_access',
        responseType: 'code',
        silentRenew: true,
        useRefreshToken: true,
        logLevel: LogLevel.Debug,
      },
    }),
  ],
  providers: [{ provide: AbstractSecurityStorage, useClass: AuthLocalStorageService }],
  exports: [AuthModule],
})
export class AuthConfigModule {}