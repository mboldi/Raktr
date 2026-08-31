import {Component, OnInit} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {MatCard, MatCardContent, MatCardHeader} from '@angular/material/card';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatButton} from '@angular/material/button';
import {MatCheckbox, MatCheckboxChange} from '@angular/material/checkbox';
import {MatSnackBar} from '@angular/material/snack-bar';
import {UserService} from '../../../services/user.service';
import {ConfigService} from '../../../services/config.service';
import {LocalStorageService} from '../../../services/localStorage.service';
import {UserDetails} from '../../../model/user/userDetails';
import {UserUpdateDto} from '../../../model/user/userUpdateDto';
import {ConfigUpdateDto} from '../../../model/config/configUpdateDto';
import {environment} from '../../../../environments/environment';

@Component({
  selector: 'app-settings',
  imports: [
    ReactiveFormsModule,
    MatCard,
    MatCardHeader,
    MatCardContent,
    MatFormField,
    MatLabel,
    MatInput,
    MatButton,
    MatCheckbox,
  ],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss',
})
export class SettingsComponent implements OnInit {
  protected admin = false;

  protected usernameFormControl = new FormControl({value: '', disabled: true});
  protected fullNameFormControl = new FormControl({value: '', disabled: true});
  protected nickNameFormControl = new FormControl();
  protected personalIdFormControl = new FormControl();

  protected ean8Forced = false;

  private currentUsername = '';

  constructor(
    private userService: UserService,
    private configService: ConfigService,
    private localStorageService: LocalStorageService,
    private snackBar: MatSnackBar,
  ) {
  }

  ngOnInit() {
    this.currentUsername = this.localStorageService.read('username') ?? '';

    this.userService.getUser(this.currentUsername).subscribe(user => {
      this.admin = user.groups.includes(environment.adminGroupName);
      this.populateUserForm(user);
    });

    this.configService.getConfigs().subscribe(configs => {
      const forceEan8Config = configs.find(config => config.key === environment.forceEan8Key);
      this.ean8Forced = forceEan8Config?.value === 'true';
    });
  }

  private populateUserForm(user: UserDetails) {
    this.usernameFormControl.setValue(user.username);
    this.fullNameFormControl.setValue(`${user.familyName} ${user.givenName}`);
    this.nickNameFormControl.setValue(user.nickname);
    this.personalIdFormControl.setValue(user.personalId);
  }

  protected updateUser() {
    this.userService.updateUser(
      this.currentUsername,
      new UserUpdateDto(this.nickNameFormControl.value, this.personalIdFormControl.value)
    ).subscribe(user => {
      this.nickNameFormControl.setValue(user.nickname);
      this.personalIdFormControl.setValue(user.personalId);

      this.snackBar.open(`Személyes adatok frissítve!`, "Remek!", {
        duration: 3000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['success-snackbar'],
      });
    });
  }

  protected updateForceEan8($event: MatCheckboxChange) {
    this.configService.updateConfig(
      environment.forceEan8Key,
      new ConfigUpdateDto($event.checked ? 'true' : 'false')
    ).subscribe(config => {
      this.ean8Forced = config.value === 'true';

      this.snackBar.open(`Vonalkód beállítás frissítve!`, "Kitűnő!", {
        duration: 3000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['success-snackbar'],
      });
    });
  }
}
