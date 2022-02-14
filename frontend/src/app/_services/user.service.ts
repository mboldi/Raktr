import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {User} from '../model/User';
import {environment} from '../../environments/environment';
import {map} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class UserService {


    constructor(private http: HttpClient) {
    }

    public getUser(username: string): Observable<User> {
        return this.http.get<User>(`${environment.apiUrl}/api/user/${username}`);
    }

    public getRentIssuerableMembers(): Observable<User[]> {
        return this.http.get<User[]>(`${environment.apiUrl}/api/user/canissuerent`)
            .pipe(
                map(users => {
                    const users_typed: User[] = [];

                    users.forEach(user => users_typed.push(User.fromJson(user)));

                    return users_typed;
                })
            );
    }

    public updateUser(user: User): Observable<User> {
        const body = `{\"User\": ${JSON.stringify(user)}}`;
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<User>(`${environment.apiUrl}/api/user/`, body, {headers: headers});
    }

    public getCurrentUser(): Observable<User> {
        return this.getUser(localStorage.getItem('username'))
    }
}
