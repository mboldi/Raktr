import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {OwnerDetailsDto} from '../model/owner/ownerDetailsDto';

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

}
