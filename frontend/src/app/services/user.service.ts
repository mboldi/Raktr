import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { UserDetails } from '../model/user/userDetails';
import { environment } from '../../environments/environment';
import { UserUpdateDto } from '../model/user/userUpdateDto';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);

  getUsers(canIssueRent?: boolean): Observable<UserDetails[]> {
    const params =
      canIssueRent !== undefined ? new HttpParams().set('canIssueRent', canIssueRent) : undefined;

    return this.http
      .get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/users`, { params })
      .pipe(
        map((users) => {
          const typedUsers: UserDetails[] = [];

          users.forEach((user) => typedUsers.push(UserDetails.fromJson(user)));

          return typedUsers;
        }),
      );
  }

  getCurrentUser(): Observable<UserDetails> {
    return this.http
      .get<Record<string, unknown>>(`${environment.apiUrl}/v1/users/me`)
      .pipe(map((user) => UserDetails.fromJson(user)));
  }

  updateUser(usernameToUpdate: string, userUpdate: UserUpdateDto): Observable<UserDetails> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http
      .put<Record<string, unknown>>(
        `${environment.apiUrl}/v1/users/${usernameToUpdate}`,
        userUpdate,
        { headers: headers },
      )
      .pipe(map((updatedUser) => UserDetails.fromJson(updatedUser)));
  }
}
