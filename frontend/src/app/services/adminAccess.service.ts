import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { UserService } from './user.service';
import { LocalStorageService } from './localStorage.service';
import { environment } from '../../environments/environment';
import { UserDetails } from '../model/user/userDetails';

@Injectable({
  providedIn: 'root',
})
export class AdminAccessService {
  private userService = inject(UserService);
  private localStorageService = inject(LocalStorageService);

  private currentUser$: Observable<UserDetails> | undefined;

  getCurrentUser(): Observable<UserDetails> {
    if (!this.currentUser$) {
      const username = this.localStorageService.read('username') ?? '';

      this.currentUser$ = this.userService.getUser(username).pipe(shareReplay(1));
    }

    return this.currentUser$;
  }

  setCurrentUser(user: UserDetails): void {
    this.currentUser$ = of(user);
  }

  isAdmin(): Observable<boolean> {
    return this.getCurrentUser().pipe(
      map((user) => user.groups.includes(environment.adminGroupName)),
    );
  }

  isFullAccessMember(): Observable<boolean> {
    return this.getCurrentUser().pipe(
      map((user) => user.groups.some((group) => environment.fullAccessGroupNames.includes(group))),
    );
  }

  /** Admins and full-access members are the only ones allowed to create new items
   * (devices, containers, rents, tickets, ...) - everyone else can only view/use them. */
  canCreateContent(): Observable<boolean> {
    return this.getCurrentUser().pipe(
      map(
        (user) =>
          user.groups.includes(environment.adminGroupName) ||
          user.groups.some((group) => environment.fullAccessGroupNames.includes(group)),
      ),
    );
  }
}
