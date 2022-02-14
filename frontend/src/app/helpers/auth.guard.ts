import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Injectable} from '@angular/core';
import {UserService} from '../_services/user.service';
import {AuthService} from '../_services/auth.service';

@Injectable({providedIn: 'root'})
export class AuthGuard implements CanActivate {

    constructor(
        private router: Router,
        private userService: UserService,
        private authService: AuthService
    ) {
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const token = localStorage.getItem('id_token');
        const expires_at = localStorage.getItem('expires_at');
        const username = localStorage.getItem('username');

        if (!this.authService.isLoggedIn()) {
            console.log('loggedin: ' + this.authService.isLoggedIn());
            this.router.navigate(['/login']);
            return false;
        }

        return true;
    }
}
