import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { AdminAccessService } from '../services/adminAccess.service';

export const createContentGuard: CanActivateFn = () => {
  const adminAccessService = inject(AdminAccessService);
  const router = inject(Router);

  return adminAccessService
    .canCreateContent()
    .pipe(map((canCreate) => (canCreate ? true : router.createUrlTree(['/unauthorized']))));
};
