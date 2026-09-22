import { Component, OnInit, ChangeDetectionStrategy, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatCard, MatCardContent, MatCardHeader } from '@angular/material/card';
import { MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from '../../../services/user.service';
import { AdminAccessService } from '../../../services/adminAccess.service';
import { ConfigService } from '../../../services/config.service';
import { UserDetails } from '../../../model/user/userDetails';
import { UserUpdateDto } from '../../../model/user/userUpdateDto';
import { ConfigUpdateDto } from '../../../model/config/configUpdateDto';
import { environment } from '../../../../environments/environment';
import { MatBadge } from '@angular/material/badge';

function blankToNull(value: string | null): string | null {
  return value?.trim() || null;
}

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
    MatBadge,
  ],
  templateUrl: './settings.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './settings.component.scss',
})
export class SettingsComponent implements OnInit {
  private userService = inject(UserService);
  private adminAccessService = inject(AdminAccessService);
  private configService = inject(ConfigService);
  private snackBar = inject(MatSnackBar);

  protected admin = false;

  protected usernameFormControl = new FormControl({ value: '', disabled: true });
  protected fullNameFormControl = new FormControl({ value: '', disabled: true });
  protected nickNameFormControl = new FormControl();
  protected personalIdFormControl = new FormControl();

  protected ean8Forced = false;

  private currentUsername = '';

  ngOnInit() {
    this.adminAccessService.getCurrentUser().subscribe((user) => this.populateUserForm(user));
    this.adminAccessService.isAdmin().subscribe((admin) => (this.admin = admin));

    this.configService.getConfigs().subscribe((configs) => {
      const forceEan8Config = configs.find((config) => config.key === environment.forceEan8Key);
      this.ean8Forced = forceEan8Config?.value === 'true';
    });
  }

  private populateUserForm(user: UserDetails) {
    this.currentUsername = user.username;
    this.usernameFormControl.setValue(user.username);
    this.fullNameFormControl.setValue(`${user.familyName} ${user.givenName}`);
    this.nickNameFormControl.setValue(user.nickname);
    this.personalIdFormControl.setValue(user.personalId);
  }

  protected updateUser() {
    this.userService
      .updateUser(
        this.currentUsername,
        new UserUpdateDto(
          blankToNull(this.nickNameFormControl.value),
          blankToNull(this.personalIdFormControl.value),
        ),
      )
      .subscribe((user) => {
        this.adminAccessService.setCurrentUser(user);

        this.nickNameFormControl.setValue(user.nickname);
        this.personalIdFormControl.setValue(user.personalId);

        this.snackBar.open(`Személyes adatok frissítve!`, 'Remek!', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['success-snackbar'],
        });
      });
  }

  protected updateForceEan8($event: MatCheckboxChange) {
    this.configService
      .updateConfig(
        environment.forceEan8Key,
        new ConfigUpdateDto($event.checked ? 'true' : 'false'),
      )
      .subscribe((config) => {
        this.ean8Forced = config.value === 'true';

        this.snackBar.open(`Vonalkód beállítás frissítve!`, 'Kitűnő!', {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['success-snackbar'],
        });
      });
  }
}
