import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {RentDetailsDto} from '../model/rent/rentDetailsDto';

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

  getRents(): Observable<RentDetailsDto[]> {
    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/rents`)
      .pipe(
        map(rents => {
          const rentsTyped: RentDetailsDto[] = [];

          rents.forEach(rent => rentsTyped.push(RentDetailsDto.fromJson(rent)));

          return rentsTyped;
        })
      )
  }

}
