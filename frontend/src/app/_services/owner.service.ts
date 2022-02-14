import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {map} from 'rxjs/operators';
import {Owner} from '../model/Owner';
import {Device} from '../model/Device';

@Injectable({
    providedIn: 'root'
})
export class OwnerService {

    constructor(private http: HttpClient) {
    }

    getOwners(): Observable<Owner[]> {
        return this.http.get<Owner[]>(`${environment.apiUrl}/api/owner`)
            .pipe(
                map(owners => {
                    const ownerss_typed: Owner[] = [];

                    owners.forEach(owner => ownerss_typed.push(Owner.fromJson(owner)))

                    return ownerss_typed;
                })
            );
    }

    updateOwner(owner: Owner): Observable<Owner> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<Owner>(`${environment.apiUrl}/api/owner/`, Owner.toJsonString(owner), {headers: headers})
            .pipe(
                map(gotOwner => Owner.fromJson(gotOwner))
            );

    }

    deleteOwner(owner: Owner): Observable<Owner> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.delete<Owner>(`${environment.apiUrl}/api/device/${owner.id}`, {headers: headers})
            .pipe(
                map(gotOwner => Owner.fromJson(gotOwner))
            );
    }
}
