import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';

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

}
