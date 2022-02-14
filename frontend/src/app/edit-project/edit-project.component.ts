import {Component, OnInit} from '@angular/core';
import {Project} from '../model/Project';
import {UserService} from '../_services/user.service';
import {Title} from '@angular/platform-browser';
import {ActivatedRoute, Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ProjectService} from '../_services/project.service';
import {User} from '../model/User';
import * as $ from 'jquery';

@Component({
    selector: 'app-project',
    templateUrl: './edit-project.component.html',
    styleUrls: ['./edit-project.component.css']
})
export class EditProjectComponent implements OnInit {

    rentIssuingMembers: User[];
    filteredRentIssuingMembers: User[];
    selectedIssuer = '';

    project: Project;

    fullAccessMember = false;
    admin = false;
    user: User;

    projectForm: FormGroup;
    private currentStartDate: Date;
    deleteConfirmed = false;

    constructor(private projectService: ProjectService,
                private userService: UserService,
                private title: Title,
                private route: ActivatedRoute,
                private modalService: NgbModal,
                private fb: FormBuilder,
                private router: Router) {

        this.projectForm = fb.group({
            projectName: ['', Validators.required],
            prodManager: ['', Validators.required],
            startDate: ['', Validators.required],
            endDate: ['', Validators.required]
        });

        this.userService.getCurrentUser().subscribe(user => {
            this.user = user;

            this.fullAccessMember = User.isFullAccessMember(user);
            this.admin = User.isAdmin(user);

            if (this.route.snapshot.paramMap.get('id') === 'new') {
                this.project.prodManager = this.user;

                this.projectForm.patchValue({
                    'prodManager': this.user.familyName + ' ' + this.user.givenName
                })

                this.selectedIssuer = this.user.username;
            }
        });

        userService.getRentIssuerableMembers().subscribe(users => {
            this.rentIssuingMembers = users;
            this.filteredRentIssuingMembers = users;
        });

        this.projectForm
            .get('prodManager')
            .valueChanges
            .subscribe(value => {
                const filteredMembers =
                    this.rentIssuingMembers.filter(user => (user.familyName + ' ' + user.givenName)
                        .toLowerCase().includes(value.toLowerCase()));

                this.filteredRentIssuingMembers = filteredMembers.length > 10 ? [] : filteredMembers;
            });

        this.currentStartDate = new Date();
    }

    ngOnInit(): void {
        const id = this.route.snapshot.paramMap.get('id');

        if (id === 'new') {
            this.project = new Project();
        } else {
            this.projectService.getProject(id).subscribe(project => {
                this.project = project;

                this.setCurrStartDate(project.expEndDate);

                this.setFormFieldsWithProjectData();
            })
        }
    }

    private setFormFieldsWithProjectData() {
        this.projectForm.setValue({
            projectName: this.project.name,
            prodManager: this.project.prodManager.familyName + ' ' + this.project.prodManager.givenName,
            startDate: this.project.startDate,
            endDate: this.project.expEndDate
        })
    }

    setSelectedIssuer(username: string) {
        this.selectedIssuer = username;

        this.userService.getUser(username).subscribe(user => this.project.prodManager = user);
    }

    setCurrStartDate(event) {
        this.currentStartDate = new Date(event.value);
    }

    save() {
        this.setProjectFields();

        if (this.project.name === '' ||
            this.project.startDate.toString() === '' ||
            this.project.expEndDate.toString() === '') {

            this.showNotification('Hiányzó adatok!', 'warning');
            return;
        }

        console.log(this.project)

        if (this.project.id === -1) {   // new
            console.log('asdasd')
            this.projectService.addProject(this.project).subscribe(project_ => {
                    this.project = project_;
                    this.showNotification('Sikeresen mentve', 'success');

                    this.router.navigateByUrl('/project/' + this.project.id);
                },
                error => {
                    this.showNotification('Nem sikerült menteni', 'warning');
                })
        }
    }

    private setProjectFields() {
        const formValue = this.projectForm.value;

        this.project.name = formValue.projectName.toString();
        this.project.startDate = formValue.startDate;
        this.project.expEndDate = formValue.endDate;
    }

    delete(project: Project) {

    }

    showNotification(message_: string, type: string) {
        $['notify']({
            icon: 'add_alert',
            message: message_
        }, {
            type: type,
            timer: 500,
            placement: {
                from: 'top',
                align: 'right'
            },
            z_index: 2000
        })
    }
}
