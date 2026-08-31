import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {catchError, map, Observable, of} from 'rxjs';
import {environment} from '../../environments/environment';
import {ContainerDetails} from '../model/scannable/container/containerDetails';
import {ContainerCreateDto} from '../model/scannable/container/containerCreateDto';
import {ContainerUpdateDto} from '../model/scannable/container/containerUpdateDto';
import {ContainerAddDevicesDto} from '../model/scannable/container/containerAddDevicesDto';
import {ContainerItemUpdateDto} from '../model/scannable/container/containerItemUpdateDto';
import {TicketDetails} from '../model/ticket/ticketDetails';
import {RentDetails} from '../model/rent/rentDetails';

@Injectable({
  providedIn: 'root'
})
export class ContainerService {

  constructor(private http: HttpClient) {
  }

  getContainers(includeDeleted: boolean = false): Observable<ContainerDetails[]> {
    const params = new HttpParams().set('deleted', includeDeleted);

    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/containers`, {params})
      .pipe(
        map(containers => {
          const containers_typed: ContainerDetails[] = [];

          containers.forEach(container => containers_typed.push(ContainerDetails.fromJson(container)));

          return containers_typed;
        })
      )
  }

  getContainer(containerId: number): Observable<ContainerDetails> {
    return this.http.get<Record<string, unknown>>(`${environment.apiUrl}/v1/containers/${containerId}`)
      .pipe(
        map(container => ContainerDetails.fromJson(container))
      )
  }

  createContainer(containerToCreate: ContainerCreateDto): Observable<ContainerDetails> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post<Record<string, unknown>>(`${environment.apiUrl}/v1/containers`, containerToCreate.toJson(), {headers: headers})
      .pipe(
        map(createdContainer => ContainerDetails.fromJson(createdContainer))
      );
  }

  updateContainer(containerId: number, containerUpdate: ContainerUpdateDto): Observable<ContainerDetails> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put<Record<string, unknown>>(`${environment.apiUrl}/v1/containers/${containerId}`, containerUpdate.toJson(), {headers: headers})
      .pipe(
        map(updatedContainer => ContainerDetails.fromJson(updatedContainer))
      );
  }

  // returns whether the delete request was successful
  deleteContainer(containerId: number): Observable<boolean> {
    return this.http.delete(`${environment.apiUrl}/v1/containers/${containerId}`, {observe: 'response'})
      .pipe(
        map(response => response.status === 200),
        catchError(error => {
          if (error.status === 404) {
            return of(false);
          }
          throw error;
        })
      );
  }

  // returns whether the restore request was successful
  restoreContainer(containerId: number): Observable<boolean> {
    return this.http.post<Record<string, unknown>>(`${environment.apiUrl}/v1/containers/${containerId}/restore`, "{}", {observe: 'response'})
      .pipe(
        map(response => response.status === 200),
        catchError(error => {
          if (error.status === 404) {
            return of(false);
          }
          throw error;
        })
      );
  }

  addDevicesToContainer(containerId: number, devicesToAdd: ContainerAddDevicesDto): Observable<ContainerDetails> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.post<Record<string, unknown>>(`${environment.apiUrl}/v1/containers/${containerId}/devices`, devicesToAdd.toJson(), {headers: headers})
      .pipe(
        map(updatedContainer => ContainerDetails.fromJson(updatedContainer))
      );
  }

  updateDeviceInContainer(containerId: number, deviceId: number, itemUpdate: ContainerItemUpdateDto): Observable<ContainerDetails> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put<Record<string, unknown>>(`${environment.apiUrl}/v1/containers/${containerId}/devices/${deviceId}`, itemUpdate.toJson(), {headers: headers})
      .pipe(
        map(updatedContainer => ContainerDetails.fromJson(updatedContainer))
      );
  }

  removeDeviceFromContainer(containerId: number, deviceId: number): Observable<ContainerDetails> {
    return this.http.delete<Record<string, unknown>>(`${environment.apiUrl}/v1/containers/${containerId}/devices/${deviceId}`)
      .pipe(
        map(updatedContainer => ContainerDetails.fromJson(updatedContainer))
      );
  }

  getTicketsOfContainer(containerId: number): Observable<TicketDetails[]> {
    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/containers/${containerId}/tickets`)
      .pipe(
        map(tickets => {
          const tickets_typed: TicketDetails[] = [];

          tickets.forEach(ticket => tickets_typed.push(TicketDetails.fromJson(ticket)));

          return tickets_typed;
        })
      )
  }

  getRentsOfContainer(containerId: number): Observable<RentDetails[]> {
    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/containers/${containerId}/rents`)
      .pipe(
        map(rents => {
          const rents_typed: RentDetails[] = [];

          rents.forEach(rent => rents_typed.push(RentDetails.fromJson(rent)));

          return rents_typed;
        })
      )
  }
}
