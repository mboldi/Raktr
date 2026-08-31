import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map, shareReplay} from 'rxjs/operators';
import {UserService} from './user.service';
import {LocalStorageService} from './localStorage.service';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminAccessService {
  private isAdmin$: Observable<boolean> | undefined;

  constructor(
    private userService: UserService,
    private localStorageService: LocalStorageService,
  ) {
  }

  isAdmin(): Observable<boolean> {
    if (!this.isAdmin$) {
      const username = this.localStorageService.read('username') ?? '';

      this.isAdmin$ = this.userService.getUser(username).pipe(
        map(user => user.groups.includes(environment.adminGroupName)),
        shareReplay(1)
      );
    }

    return this.isAdmin$;
  }
}
