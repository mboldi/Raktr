import {Injectable} from '@angular/core';
import {Location} from '../model/Location';
import {Observable, of} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {map} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class LocationService {

    constructor(private http: HttpClient) {
    }

    getLocations(): Observable<Location[]> {
        return this.http.get<Location[]>(`${environment.apiUrl}/api/location`)
            .pipe(
                map(locations => {
                    const locations_typed: Location[] = [];

                    locations.forEach(location => locations_typed.push(Location.fromJson(location)))

                    return locations_typed
                })
            );
    }
}
