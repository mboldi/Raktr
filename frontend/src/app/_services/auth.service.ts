import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {UserService} from './user.service';
// @ts-ignore
import moment = require('moment');

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    constructor(private http: HttpClient,
                private userService: UserService) {
    }

    // @ts-ignore
    login(username: string, password: string) {
        const response = this.http.post<any>(`${environment.apiUrl}/login`, {username, password}, {observe: 'response'});
        response.subscribe(resp => {
            this.setSession(resp, username);
        });
        return response;
    }

    setSession(authResult, username) {
        localStorage.setItem('id_token', authResult.body.token);
        localStorage.setItem('expires_at', authResult.body.expiresAt);
        //console.log(authResult.body.expiresAt);
        localStorage.setItem('username', username);
    }

    logout() {
        localStorage.removeItem('id_token');
        localStorage.removeItem('username');
        localStorage.removeItem('expires_at');
    }

    public isLoggedIn() {
        return localStorage.getItem('id_token') !== null;
    }

    isLoggedOut() {
        return !this.isLoggedIn();
    }

    getExpiration() {
        const expiration = localStorage.getItem('expires_at');
        const expiresAt = JSON.parse(expiration);
        return moment(expiresAt);
    }
}
