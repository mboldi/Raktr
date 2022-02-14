import {Component, OnInit} from '@angular/core';
import {AuthService} from '../_services/auth.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {Title} from '@angular/platform-browser';
import {catchError, delay, timeout} from 'rxjs/operators';
import {of} from 'rxjs';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
    hide = true;
    loginHappening = false;
    invalidCreds = false;
    form: FormGroup;

    constructor(private authService: AuthService,
                private fb: FormBuilder,
                private router: Router,
                private title: Title) {
        title.setTitle('Raktr - bejelentkezés');

        this.form = this.fb.group({
            username: ['', Validators.required],
            password: ['', Validators.required]
        });
    }

    ngOnInit(): void {
        if (this.authService.isLoggedIn()) {
            this.router.navigateByUrl('/overview');
        }
    }

    login() {
        this.loginHappening = true;
        this.invalidCreds = false;
        const val = this.form.value;

        if (val.username && val.password) {
            this.authService.login(val.username, val.password).pipe(delay(50)).subscribe(resp => {
                    if (resp.ok) {
                        this.router.navigateByUrl('/overview');
                    }
                },
                error => {
                    this.loginHappening = false;
                    this.invalidCreds = true;
                });
        }
    }
}
