import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {map} from 'rxjs/operators';
import {Ticket} from '../_model/Ticket';

@Injectable({
    providedIn: 'root'
})
export class TicketService {

    constructor(private http: HttpClient) {
    }

    getTickets(): Observable<Ticket[]> {
        return this.http.get<Ticket[]>(`${environment.apiUrl}/api/ticket`)
            .pipe(
                map(tickets => {
                    const tickets_typed: Ticket[] = [];

                    tickets.forEach(ticket => tickets_typed.push(Ticket.fromJson(ticket)))

                    return tickets_typed;
                })
            );
    }

    addTicket(ticket: Ticket): Observable<Ticket> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.post<Ticket>(`${environment.apiUrl}/api/ticket`, ticket.toJson, {headers: headers})
            .pipe(
                map(ticket_ => Ticket.fromJson(ticket_))
            );
    }

    updateTicket(ticket: Ticket): Observable<Ticket> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<Ticket>(`${environment.apiUrl}/api/ticket`, ticket.toJson, {headers: headers})
            .pipe(
                map(ticket_ => Ticket.fromJson(ticket_))
            );
    }

    deleteTicket(ticket: Ticket): Observable<Ticket> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.request<Ticket>('delete', `${environment.apiUrl}/api/ticket`,
            {headers: headers, body: ticket.toJson})
            .pipe(
                map(ticket_ => Ticket.fromJson(ticket_))
            );
    }
}
