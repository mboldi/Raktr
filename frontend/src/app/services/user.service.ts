import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {UserDetails} from '../model/user/userDetails';
import {environment} from '../../environments/environment';
import {UserUpdateDto} from '../model/user/userUpdateDto';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) {
  }

  getUsers(): Observable<UserDetails[]> {
    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/users`)
      .pipe(
        map(users => {
          let typedUsers: UserDetails[] = [];

          users.forEach(user => typedUsers.push(UserDetails.fromJson(user)))

          return typedUsers;
        })
      )
  }

  getUser(username: string): Observable<UserDetails> {
    return this.http.get<Record<string, unknown>>(`${environment.apiUrl}/v1/users/${username}`)
      .pipe(
        map(user => {
          return UserDetails.fromJson(user);
        })
      )
  }

  updateUser(usernameToUpdate: string, userUpdate: UserUpdateDto): Observable<UserDetails> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put<Record<string, unknown>>(`${environment.apiUrl}/v1/users/${usernameToUpdate}`, userUpdate, {headers: headers})
      .pipe(
        map(updatedUser => UserDetails.fromJson(updatedUser))
      );
  }

}
