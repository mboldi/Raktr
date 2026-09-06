import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {TicketDetails} from '../model/ticket/ticketDetails';
import {TicketCreateDto} from '../model/ticket/ticketCreateDto';
import {TicketUpdateDto} from '../model/ticket/ticketUpdateDto.ty';
import {CommentCreateDto} from '../model/comment/commentCreateDto';
import {CommentDetailsDto} from '../model/comment/commentDetailsDto';

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

  createTicket(ticketToCreate: TicketCreateDto): Observable<TicketDetails> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post<Record<string, unknown>>(`${environment.apiUrl}/v1/tickets`, ticketToCreate.toJson(), {headers: headers})
      .pipe(
        map(createdTicket => TicketDetails.fromJson(createdTicket))
      );
  }

  updateTicket(ticketId: number, ticketUpdate: TicketUpdateDto): Observable<TicketDetails> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put<Record<string, unknown>>(`${environment.apiUrl}/v1/tickets/${ticketId}`, ticketUpdate.toJson(), {headers: headers})
      .pipe(
        map(updatedTicket => TicketDetails.fromJson(updatedTicket))
      );
  }

  addComment(ticketId: number, comment: CommentCreateDto): Observable<CommentDetailsDto> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post<Record<string, unknown>>(`${environment.apiUrl}/v1/tickets/${ticketId}/comments`, comment, {headers: headers})
      .pipe(
        map(createdComment => CommentDetailsDto.fromJson(createdComment))
      );
  }

}
