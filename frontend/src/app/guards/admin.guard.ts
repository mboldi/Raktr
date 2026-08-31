import {inject} from '@angular/core';
import {CanActivateFn, Router} from '@angular/router';
import {map} from 'rxjs/operators';
import {AdminAccessService} from '../services/adminAccess.service';

export const adminGuard: CanActivateFn = () => {
  const adminAccessService = inject(AdminAccessService);
  const router = inject(Router);

  return adminAccessService.isAdmin().pipe(
    map(isAdmin => isAdmin ? true : router.createUrlTree(['/unauthorized']))
  );
};
