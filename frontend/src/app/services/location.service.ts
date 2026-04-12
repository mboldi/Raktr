import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {LocationDetails} from '../model/location/LocationDetails';

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

  deleteCategory(name: string) {
    return this.http.delete(`${environment.apiUrl}/v1/locations/${name}`);
  }


}
