import {Component, OnInit} from '@angular/core';
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCard, MatCardContent, MatCardHeader} from "@angular/material/card";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {MatButton} from '@angular/material/button';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ConfigService} from '../../../../services/config.service';
import {ConfigDetailsDto} from '../../../../model/config/configDetailsDto';
import {environment} from '../../../../../environments/environment';
import {ConfigUpdateDto} from '../../../../model/config/configUpdateDto';
import {forkJoin} from 'rxjs';

@Component({
  selector: 'app-signers',
  imports: [
    FormsModule,
    MatCard,
    MatFormField,
    MatInput,
    MatLabel,
    MatCardContent,
    MatCardHeader,
    ReactiveFormsModule,
    MatButton
  ],
  templateUrl: './signers.component.html',
  styleUrl: './signers.component.scss',
})
export class SignersComponent implements OnInit {
  protected groupLeaderNameFormControl: FormControl = new FormControl();
  protected groupNameFormControl: FormControl = new FormControl();
  protected firstSignerNameFormControl: FormControl = new FormControl();
  protected firstSignerTitleFormControl: FormControl = new FormControl();
  protected secondSignerNameFormControl: FormControl = new FormControl();
  protected secondSignerTitleFormControl: FormControl = new FormControl();

  constructor(
    private configService: ConfigService,
    private snackBar: MatSnackBar,
  ) {
  }

  ngOnInit() {
    this.configService.getConfigs().subscribe( configs => {
      this.groupNameFormControl.setValue(this.getConfigValue(configs, environment.rentTeamNameKey)?.value);
      this.groupLeaderNameFormControl.setValue(this.getConfigValue(configs, environment.rentTeamLeaderKey)?.value);

      this.firstSignerNameFormControl.setValue(this.getConfigValue(configs, environment.rentFirstSignerNameKey)?.value);
      this.firstSignerTitleFormControl.setValue(this.getConfigValue(configs, environment.rentFirstSignerTitleKey)?.value);

      this.secondSignerNameFormControl.setValue(this.getConfigValue(configs, environment.rentSecondSignerNameKey)?.value);
      this.secondSignerTitleFormControl.setValue(this.getConfigValue(configs, environment.rentSecondSignerTitleKey)?.value);
    })
  }

  private getConfigValue(configs: ConfigDetailsDto[], key: string): ConfigDetailsDto | undefined {
    for (let i = 0, len = configs.length; i < len; i++) {
      if(configs[i].key === key) {
        return configs[i];
      }
    }

    return undefined;
  }

  protected saveGroupData() {
    forkJoin([
      this.configService.updateConfig(
        environment.rentTeamNameKey,
        new ConfigUpdateDto(this.groupNameFormControl.value)
      ),
      this.configService.updateConfig(
        environment.rentTeamLeaderKey,
        new ConfigUpdateDto(this.groupLeaderNameFormControl.value)
      ),
    ]).subscribe({
      next: ([groupName, groupLeaderName]) => {
        this.groupNameFormControl.setValue(groupName.value);
        this.groupLeaderNameFormControl.setValue(groupLeaderName.value);

        this.snackBar.open(`Körös adatok frissítve!`, "Remek!", {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['success-snackbar'],
        });
      },
      error: () => {
        this.snackBar.open(`Nem sikerült menteni a körös adatokat!`, "Értem", {
          duration: 4000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['error-snackbar'],
        });
      }
    });
  }

  protected saveGlobalData() {
    forkJoin([
      this.configService.updateConfig(
        environment.rentFirstSignerNameKey,
        new ConfigUpdateDto(this.firstSignerNameFormControl.value)
      ),
      this.configService.updateConfig(
        environment.rentFirstSignerTitleKey,
        new ConfigUpdateDto(this.firstSignerTitleFormControl.value)
      ),
      this.configService.updateConfig(
        environment.rentSecondSignerNameKey,
        new ConfigUpdateDto(this.secondSignerNameFormControl.value)
      ),
      this.configService.updateConfig(
        environment.rentSecondSignerTitleKey,
        new ConfigUpdateDto(this.secondSignerTitleFormControl.value)
      ),
    ]).subscribe({
      next: ([firstSignerName, firstSignerTitle, secondSignerName, secondSignerTitle]) => {
        this.firstSignerNameFormControl.setValue(firstSignerName.value);
        this.firstSignerTitleFormControl.setValue(firstSignerTitle.value);
        this.secondSignerNameFormControl.setValue(secondSignerName.value);
        this.secondSignerTitleFormControl.setValue(secondSignerTitle.value);

        this.snackBar.open(`Aláírói adatok frissítve!`, "Remek!", {
          duration: 3000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['success-snackbar'],
        });
      },
      error: () => {
        this.snackBar.open(`Nem sikerült menteni az aláírói adatokat!`, "Értem", {
          duration: 4000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['error-snackbar'],
        });
      }
    });
  }
}
