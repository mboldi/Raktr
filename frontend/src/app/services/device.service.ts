import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, map, Observable, of} from 'rxjs';
import {environment} from '../../environments/environment';
import {DeviceDetails} from "../model/scannable/device/deviceDetails";
import {DeviceUpdateDto} from '../model/scannable/device/deviceUpdateDto';
import {DeviceCreateDto} from '../model/scannable/device/deviceCreateDto';
import {TicketDetails} from '../model/ticket/ticketDetails';
import {RentDetails} from '../model/rent/rentDetails';

@Injectable({
  providedIn: 'root'
})
export class DeviceService {

  constructor(private http: HttpClient) {
  }

  getDevices(): Observable<DeviceDetails[]> {
    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/devices`)
      .pipe(
        map(devices => {
          const devices_typed: DeviceDetails[] = [];

          devices.forEach(device => devices_typed.push(DeviceDetails.fromJson(device)));

          return devices_typed;
        })
      )
  }

  getDevice(deviceId: number): Observable<DeviceDetails> {
    return this.http.get<Record<string, unknown>>(`${environment.apiUrl}/v1/devices/${deviceId}`)
      .pipe(
        map(device => {
          return DeviceDetails.fromJson(device);
        })
      )
  }

  updateDevice(deviceId: number, deviceUpdate: DeviceUpdateDto): Observable<DeviceDetails> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    return this.http.put<Record<string, unknown>>(`${environment.apiUrl}/v1/devices/${deviceId}`, deviceUpdate.toJson(), {headers: headers})
      .pipe(
        map(updatedDevice => DeviceDetails.fromJson(updatedDevice))
      );
  }

  // returns whether the delete request was successful
  deleteDevice(deviceId: number): Observable<boolean> {
    return this.http.delete(`${environment.apiUrl}/v1/devices/${deviceId}`, {observe: 'response'})
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

  createDevice(deviceToCreate: DeviceCreateDto): Observable<DeviceDetails> {
    const headers = new HttpHeaders().set('Content-Type', 'application/json');

    console.log(deviceToCreate.toJson());

    return this.http.post<Record<string, unknown>>(`${environment.apiUrl}/v1/devices`, deviceToCreate.toJson(), {headers: headers})
      .pipe(
        map(createdDevice => DeviceDetails.fromJson(createdDevice))
      );
  }

  // returns whether the restore request was successful
  restoreDevice(deviceIdToRestore: number): Observable<boolean> {
    return this.http.post<Record<string, unknown>>(`${environment.apiUrl}/v1/devices/${deviceIdToRestore}/restore`, "{}", {observe: 'response'})
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

  getTicketsOfDevice(deviceId: number): Observable<TicketDetails[]> {
    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/devices/${deviceId}/tickets`)
      .pipe(
        map(tickets => {
          const tickets_typed: TicketDetails[] = [];

          tickets.forEach(ticket => tickets_typed.push(TicketDetails.fromJson(ticket)));

          return tickets_typed;
        })
      )
  }

  getRentsOfDevice(deviceId: number): Observable<RentDetails[]> {
    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/devices/${deviceId}/rents`)
      .pipe(
        map(tickets => {
          const rents_typed: RentDetails[] = [];

          tickets.forEach(rent => rents_typed.push(RentDetails.fromJson(rent)));

          return rents_typed;
        })
      )
  }

  getManufacturers(): Observable<string[]> {
    return this.http.get<string[]>(`${environment.apiUrl}/v1/devices/manufacturers`);
  }

}
