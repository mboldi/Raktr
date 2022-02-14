import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Rent} from '../model/Rent';
import {environment} from '../../environments/environment';
import {map} from 'rxjs/operators';
import {Project} from '../model/Project';

@Injectable({
    providedIn: 'root'
})
export class ProjectService {

    constructor(private http: HttpClient) {
    }

    getProjects(): Observable<Project[]> {
        return this.http.get<Project[]>(`${environment.apiUrl}/api/project`)
            .pipe(
                map(projects => {
                    const projects_typed: Project[] = [];

                    projects.forEach(project => projects_typed.push(Project.fromJson(project)))

                    return projects_typed;
                })
            );
    }

    addProject(project: Project): Observable<Project> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        console.log(Project.toJsonString(project));

        return this.http.post<Project>(`${environment.apiUrl}/api/project`, Project.toJsonString(project), {headers: headers})
            .pipe(
                map(project_ => Project.fromJson(project_))
            );
    }

    updateProject(project: Project): Observable<Project> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<Project>(`${environment.apiUrl}/api/project`, Project.toJsonString(project), {headers: headers})
            .pipe(
                map(project_ => Project.fromJson(project_))
            );
    }

    addNewRentToProject(projectId: number, newRent: Rent): Observable<Project> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<Project>(`${environment.apiUrl}/api/project/${projectId}`, Rent.toJsonString(newRent), {headers: headers})
            .pipe(
                map(project_ => Project.fromJson(project_))
            );
    }

    getProject(id: number | string): Observable<Project> {
        return this.http.get<Project>(`${environment.apiUrl}/api/project/${id}`)
            .pipe(
                map(project_ => Project.fromJson(project_))
            );
    }

    deleteProject(project: Project): Observable<Project> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.request<Project>('delete', `${environment.apiUrl}/api/rent`,
            {headers: headers, body: Project.toJsonString(project)})
            .pipe(
                map(project_ => Project.fromJson(project_))
            );
    }
}
