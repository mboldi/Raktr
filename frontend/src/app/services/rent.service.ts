import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {RentDetails} from '../model/rent/rentDetails';

@Injectable({
  providedIn: 'root'
})
export class RentService {

  constructor(private http: HttpClient) {
  }

  getRentCount(): Observable<number> {
    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/rents`)
      .pipe(
        map(rents => {
          return rents.length;
        })
      )
  }

  getRents(): Observable<RentDetails[]> {
    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/rents`)
      .pipe(
        map(rents => {
          const rentsTyped: RentDetails[] = [];

          rents.forEach(rent => rentsTyped.push(RentDetails.fromJson(rent)));

          return rentsTyped;
        })
      )
  }

}
