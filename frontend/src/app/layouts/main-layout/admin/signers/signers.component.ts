import {Component, OnInit} from '@angular/core';
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCard, MatCardContent, MatCardHeader} from "@angular/material/card";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {MatButton} from '@angular/material/button';
import {ConfigService} from '../../../../services/config.service';
import {ConfigDetailsDto} from '../../../../model/config/configDetailsDto';
import {environment} from '../../../../../environments/environment';
import {ConfigUpdateDto} from '../../../../model/config/configUpdateDto';

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

  constructor(private configService: ConfigService) {
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
    this.configService.updateConfig(
      environment.rentTeamNameKey,
      new ConfigUpdateDto(this.groupNameFormControl.value)
    ).subscribe( config => {
      this.groupNameFormControl.setValue(config.value);
    });

    this.configService.updateConfig(
      environment.rentTeamLeaderKey,
      new ConfigUpdateDto(this.groupLeaderNameFormControl.value)
    ).subscribe( config => {
      this.groupLeaderNameFormControl.setValue(config.value);
    });
  }

  protected saveGlobalData() {
    this.configService.updateConfig(
      environment.rentFirstSignerNameKey,
      new ConfigUpdateDto(this.firstSignerNameFormControl.value)
    ).subscribe( config => {
      this.firstSignerNameFormControl.setValue(config.value);
    });

    this.configService.updateConfig(
      environment.rentFirstSignerTitleKey,
      new ConfigUpdateDto(this.firstSignerTitleFormControl.value)
    ).subscribe( config => {
      this.firstSignerTitleFormControl.setValue(config.value);
    });

    this.configService.updateConfig(
      environment.rentSecondSignerNameKey,
      new ConfigUpdateDto(this.secondSignerNameFormControl.value)
    ).subscribe( config => {
      this.secondSignerNameFormControl.setValue(config.value);
    });

    this.configService.updateConfig(
      environment.rentSecondSignerTitleKey,
      new ConfigUpdateDto(this.secondSignerTitleFormControl.value)
    ).subscribe( config => {
      this.secondSignerTitleFormControl.setValue(config.value);
    });
  }
}
