import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, map, Observable, of} from 'rxjs';
import {environment} from '../../environments/environment';
import {OwnerDetailsDto} from '../model/owner/ownerDetailsDto';
import {OwnerCreateDto} from "../model/owner/ownerCreateDto";
import {OwnerUpdateDto} from "../model/owner/ownerUpdateDto";

@Injectable({
  providedIn: 'root'
})
export class OwnerService {

  constructor(private http: HttpClient) {
  }

  getOwners(): Observable<OwnerDetailsDto[]> {
    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/owners`)
      .pipe(
        map(owners => {
          const owners_typed: OwnerDetailsDto[] = [];

          owners.forEach(owner => owners_typed.push(OwnerDetailsDto.fromJson(owner)));

          return owners_typed;
        })
      )
  }

  addOwner(newOwner: OwnerCreateDto): Observable<OwnerDetailsDto> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post<Record<string, unknown>>(`${environment.apiUrl}/v1/owners`, newOwner, {headers: headers})
      .pipe(
        map(createdOwner => OwnerDetailsDto.fromJson(createdOwner))
      );
  }

  updateOwner(ownerId: number, ownerUpdate: OwnerUpdateDto): Observable<OwnerDetailsDto> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put<Record<string, unknown>>(`${environment.apiUrl}/v1/owners/${ownerId}`, ownerUpdate, {headers: headers})
      .pipe(
        map(updatedOwner => OwnerDetailsDto.fromJson(updatedOwner))
      );
  }

  // returns whether the delete request was successful
  deleteOwner(ownerId: number): Observable<boolean> {
    return this.http.delete(`${environment.apiUrl}/v1/owners/${ownerId}`, {observe: 'response'})
      .pipe(
        map(response => {
          console.log(response);
          return response.status === 204
        }),
        catchError(error => {
          if (error.status === 409) {
            return of(false);
          }
          throw error;
        })
      );
  }
}
