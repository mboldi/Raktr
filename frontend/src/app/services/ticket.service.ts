import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TicketService {

  constructor(private http: HttpClient) {
  }

  getTicketCount(): Observable<number> {
    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/tickets`)
      .pipe(
        map(tickets => {
          return tickets.length;
        })
      )
  }

}
