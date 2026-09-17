import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {LocationDetails} from '../model/location/LocationDetails';
import {LocationCreateDto} from "../model/location/locationCreateDto";

@Injectable({
    providedIn: 'root'
})
export class LocationService {

    constructor(private http: HttpClient) {
    }

    getLocations(): Observable<LocationDetails[]> {
        return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/locations`)
            .pipe(
                map(locations => {
                    const locations_typed: LocationDetails[] = [];

                    locations.forEach(location => locations_typed.push(LocationDetails.fromJson(location)));

                    return locations_typed;
                })
            )
    }

    addLocation(name: string): Observable<LocationDetails> {
        const newLocation = new LocationCreateDto(name);

        return this.http.post<Record<string, unknown>>(
            `${environment.apiUrl}/v1/locations`,
            newLocation
        ).pipe(
            map(json => LocationDetails.fromJson(json))
        );
    }

    deleteLocation(name: string) {
        return this.http.delete(`${environment.apiUrl}/v1/locations/${name}`);
    }


}
