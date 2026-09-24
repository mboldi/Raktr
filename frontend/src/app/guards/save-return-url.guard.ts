import { inject } from '@angular/core';
import { CanActivateFn, RouterStateSnapshot } from '@angular/router';
import { map, take } from 'rxjs/operators';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { LocalStorageService } from '../services/localStorage.service';

/** Runs alongside autoLoginPartialRoutesGuard - if the user isn't logged in yet, this
 * remembers the URL they actually tried to open so the login page can send them there
 * once they're authenticated, instead of always landing on the overview page. */
export const saveReturnUrlGuard: CanActivateFn = (_route, state: RouterStateSnapshot) => {
  const oidcSecurityService = inject(OidcSecurityService);
  const localStorageService = inject(LocalStorageService);

  return oidcSecurityService.isAuthenticated$.pipe(
    take(1),
    map(({ isAuthenticated }) => {
      if (!isAuthenticated) {
        localStorageService.write('returnUrl', state.url);
      }

      return true;
    }),
  );
};
