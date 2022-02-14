import {Injectable} from '@angular/core';
import {Location} from '../model/Location';
import {Observable, of} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class LocationService {

    constructor(private http: HttpClient) {
    }

    getLocations(): Observable<Location[]> {
        return this.http.get<Location[]>(`${environment.apiUrl}/api/location`);
    }
}
