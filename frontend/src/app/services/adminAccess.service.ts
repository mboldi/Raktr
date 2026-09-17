import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map, shareReplay} from 'rxjs/operators';
import {UserService} from './user.service';
import {LocalStorageService} from './localStorage.service';
import {environment} from '../../environments/environment';
import {UserDetails} from '../model/user/userDetails';

@Injectable({
  providedIn: 'root'
})
export class AdminAccessService {
  private currentUser$: Observable<UserDetails> | undefined;

  constructor(
    private userService: UserService,
    private localStorageService: LocalStorageService,
  ) {
  }

  getCurrentUser(): Observable<UserDetails> {
    if (!this.currentUser$) {
      const username = this.localStorageService.read('username') ?? '';

      this.currentUser$ = this.userService.getUser(username).pipe(shareReplay(1));
    }

    return this.currentUser$;
  }

  isAdmin(): Observable<boolean> {
    return this.getCurrentUser().pipe(map(user => user.groups.includes(environment.adminGroupName)));
  }

  isFullAccessMember(): Observable<boolean> {
    return this.getCurrentUser().pipe(
      map(user => user.groups.some(group => environment.fullAccessGroupNames.includes(group)))
    );
  }
}
