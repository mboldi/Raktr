import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {RentDetails} from '../model/rent/rentDetails';
import {RentCreateDto} from '../model/rent/rentCreateDto';
import {RentUpdateDto} from '../model/rent/rentUpdateDto';
import {RentItemDetailsDto} from '../model/rent/rentItem/rentItemDetails';
import {RentItemCreateDto} from '../model/rent/rentItem/rentItemCreateDto';
import {RentItemUpdateDto} from '../model/rent/rentItem/rentItemUpdateDto';
import {CommentCreateDto} from '../model/comment/commentCreateDto';
import {CommentDetailsDto} from '../model/comment/commentDetailsDto';
import {RentPdfCreateDto} from '../model/rent/rentPdfCreateDto';

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

  getRent(rentId: number): Observable<RentDetails> {
    return this.http.get<Record<string, unknown>>(`${environment.apiUrl}/v1/rents/${rentId}`)
      .pipe(
        map(rent => RentDetails.fromJson(rent))
      )
  }

  createRent(rentToCreate: RentCreateDto): Observable<RentDetails> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post<Record<string, unknown>>(`${environment.apiUrl}/v1/rents`, rentToCreate.toJson(), {headers: headers})
      .pipe(
        map(createdRent => RentDetails.fromJson(createdRent))
      );
  }

  updateRent(rentId: number, rentUpdate: RentUpdateDto): Observable<RentDetails> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put<Record<string, unknown>>(`${environment.apiUrl}/v1/rents/${rentId}`, rentUpdate.toJson(), {headers: headers})
      .pipe(
        map(updatedRent => RentDetails.fromJson(updatedRent))
      );
  }

  deleteRent(rentId: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/v1/rents/${rentId}`);
  }

  addRentItem(rentId: number, itemToAdd: RentItemCreateDto): Observable<RentItemDetailsDto> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post<Record<string, unknown>>(`${environment.apiUrl}/v1/rents/${rentId}/items`, itemToAdd, {headers: headers})
      .pipe(
        map(createdItem => RentItemDetailsDto.fromJson(createdItem))
      );
  }

  updateRentItem(rentId: number, rentItemId: number, itemUpdate: RentItemUpdateDto): Observable<RentItemDetailsDto> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put<Record<string, unknown>>(`${environment.apiUrl}/v1/rents/${rentId}/items/${rentItemId}`, itemUpdate.toJson(), {headers: headers})
      .pipe(
        map(updatedItem => RentItemDetailsDto.fromJson(updatedItem))
      );
  }

  deleteRentItem(rentId: number, rentItemId: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/v1/rents/${rentId}/items/${rentItemId}`);
  }

  addComment(rentId: number, comment: CommentCreateDto): Observable<CommentDetailsDto> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post<Record<string, unknown>>(`${environment.apiUrl}/v1/rents/${rentId}/comments`, comment, {headers: headers})
      .pipe(
        map(createdComment => CommentDetailsDto.fromJson(createdComment))
      );
  }

  getRentPdf(rentId: number, pdfRequest: RentPdfCreateDto): Observable<Blob> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post(`${environment.apiUrl}/v1/rents/${rentId}/pdf`, pdfRequest, {headers: headers, responseType: 'blob'});
  }

}
