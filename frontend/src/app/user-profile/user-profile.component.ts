import {Component, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {UserService} from '../_services/user.service';
import {User} from '../model/User';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {GeneralDataService} from '../_services/general-data.service';
import {GeneralData} from '../model/GeneralData';
import * as $ from 'jquery';

@Component({
    selector: 'app-user-profile',
    templateUrl: './user-profile.component.html',
    styleUrls: ['./user-profile.component.css'],
    providers: [Title]
})
export class UserProfileComponent implements OnInit {
    user: User;
    admin = false;

    personal_settings: FormGroup;
    group_settings: FormGroup;
    global_settings: FormGroup;

    constructor(private title: Title,
                private fb: FormBuilder,
                private userService: UserService,
                private generalDataService: GeneralDataService) {
        this.title.setTitle('Raktr - Adatok szerkesztése');

        this.personal_settings = fb.group({
            username: [{value: '', disabled: true}],
            fullName: [{value: '', disabled: true}],
            nickName: [''],
            personalId: ['']
        });

        this.group_settings = fb.group({
            groupName: ['', Validators.required],
            groupLeaderName: ['', Validators.required],
        });

        this.global_settings = fb.group({
            firstSignerName: ['', Validators.required],
            firstSignerTitle: ['', Validators.required],
            secondSignerName: ['', Validators.required],
            secondSignerTitle: ['', Validators.required],
        });

        this.userService.getCurrentUser().subscribe(user => {
            this.personal_settings.setValue({
                username: [user.username],
                fullName: [user.familyName + ' ' + user.givenName],
                nickName: [user.nickName],
                personalId: [user.personalId]
            });
            this.user = user;

            this.admin = User.isFullAccessMember(user);
        });

        this.generalDataService.getAll().subscribe(data => {
            this.group_settings.setValue({
                groupName: [data[data.findIndex(data_ => data_.key === 'groupName')].data],
                groupLeaderName: [data[data.findIndex(data_ => data_.key === 'groupLeader')].data],
            });

            this.global_settings.setValue({
                firstSignerName: [data[data.findIndex(data_ => data_.key === 'firstSignerName')].data],
                firstSignerTitle: [data[data.findIndex(data_ => data_.key === 'firstSignerTitle')].data],
                secondSignerName: [data[data.findIndex(data_ => data_.key === 'secondSignerName')].data],
                secondSignerTitle: [data[data.findIndex(data_ => data_.key === 'secondSignerTitle')].data],
            });
        });
    }

    ngOnInit() {
    }

    updateUser() {
        this.user.nickName = this.personal_settings.value.nickName.toString();
        this.user.personalId = this.personal_settings.value.personalId.toString();

        this.userService.updateUser(this.user).subscribe(user => {
            this.showNotification('Személyes beállítások sikeresen mentve!', 'success')
        });
    }

    updateGroupData() {
        const val = this.group_settings.value;

        if (val.groupName.toString() !== '' &&
            val.groupLeaderName.toString() !== '') {
            const groupNameData = new GeneralData('groupName', val.groupName.toString());
            const groupLeaderNameData = new GeneralData('groupLeader', val.groupLeaderName.toString());

            this.generalDataService.updateData(groupNameData).subscribe();
            this.generalDataService.updateData(groupLeaderNameData)
                .subscribe(data => this.showNotification('Körös beállítások sikeresen mentve!', 'success'));
        } else {
            this.showNotification('Töltsd ki az összes mezőt!', 'warning');
        }
    }

    updateGlobalData() {
        const val = this.global_settings.value;

        if (val.firstSignerName.toString() !== '' &&
            val.firstSignerTitle.toString() !== '' &&
            val.secondSignerName.toString() !== '' &&
            val.secondSignerTitle.toString() !== '') {
            const firstSignerName = new GeneralData('firstSignerName', val.firstSignerName.toString());
            const firstSignerTitle = new GeneralData('firstSignerTitle', val.firstSignerTitle.toString());
            const secondSignerName = new GeneralData('secondSignerName', val.secondSignerName.toString());
            const secondSignerTitle = new GeneralData('secondSignerTitle', val.secondSignerTitle.toString());

            this.generalDataService.updateData(firstSignerName).subscribe();
            this.generalDataService.updateData(firstSignerTitle).subscribe();
            this.generalDataService.updateData(secondSignerName).subscribe();
            this.generalDataService.updateData(secondSignerTitle)
                .subscribe(data => this.showNotification('Globális beállítások sikeresen mentve!', 'success'));
        } else {
            this.showNotification('Töltsd ki az összes mezőt!', 'warning');
        }
    }

    showNotification(message_: string, type: string) {
        $['notify']({
            icon: 'add_alert',
            message: message_
        }, {
            type: type,
            timer: 1000,
            placement: {
                from: 'top',
                align: 'right'
            },
            z_index: 2000
        })
    }
}
