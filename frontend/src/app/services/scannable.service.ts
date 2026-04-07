import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ScannableService {

  constructor(private http: HttpClient) {
  }

  getScannablesCount(): Observable<number> {
    return this.http.get<number>(`${environment.apiUrl}/v1/scannables/count`)
      .pipe(
        map(count => {
          return count;
        })
      )
  }

}
