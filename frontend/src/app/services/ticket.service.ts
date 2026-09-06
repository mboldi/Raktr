import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {TicketDetails} from '../model/ticket/ticketDetails';

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

  getTickets(): Observable<TicketDetails[]> {
    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/tickets`)
      .pipe(
        map(tickets => {
          const ticketsTyped: TicketDetails[] = [];

          tickets.forEach(ticket => ticketsTyped.push(TicketDetails.fromJson(ticket)));

          return ticketsTyped;
        })
      )
  }

}
