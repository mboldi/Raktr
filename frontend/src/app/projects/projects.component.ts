import {Component, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {MatPaginatorIntl, PageEvent} from '@angular/material/paginator';
import {HunPaginator} from '../helpers/hun-paginator';
import {Project} from '../model/Project';
import {FormControl} from '@angular/forms';
import {Router} from '@angular/router';
import {ProjectService} from '../_services/project.service';

@Component({
    selector: 'app-projects',
    templateUrl: './projects.component.html',
    styleUrls: ['./projects.component.css'],
    providers: [Title,
        {provide: MatPaginatorIntl, useClass: HunPaginator}]
})
export class ProjectsComponent implements OnInit {
    projects: Project[] = [];
    filteredProjects: Project[] = [];
    pagedProjects: Project[] = [];

    currPageIndex = 0;
    currPageSize = 10;

    projectSearchControl = new FormControl();

    constructor(private title: Title,
                private projectService: ProjectService,
                private router: Router) {
        this.title.setTitle('Raktr - Projektek')
    }

    ngOnInit(): void {
        this.projectSearchControl.setValue('');

        this.projectService.getProjects().subscribe(projects => {
            this.projects = projects.sort((a, b) => {
                return a.startDate.getTime() - b.startDate.getTime();
            });

            this.projectSearchControl.setValue('');

            this.setPage();
        });

        this.projectSearchControl.valueChanges.subscribe(value => {
            this.filteredProjects = this._filterProjects(this.projects, value)
        });
    }

    private _filterProjects(projects: Project[], value: string): Project[] {
        const filterValue = value.toLowerCase();

        return projects.filter(project => project.name.toLowerCase().includes(filterValue));
    }

    private setPage() {
        for (; this.filteredProjects.length < this.currPageIndex * this.currPageSize; this.currPageIndex--) {
        }

        this.pagedProjects = this.filteredProjects.slice(this.currPageIndex * this.currPageSize,
            (this.currPageIndex + 1) * this.currPageSize);
    }

    pageChanged(event: PageEvent) {
        this.currPageIndex = event.pageIndex;
        this.currPageSize = event.pageSize;

        this.setPage();
    }

    openProject(id: number) {
        this.router.navigateByUrl(`/project/${id}`);
    }

}
