import {Injectable} from '@angular/core';
import {Device} from '../model/Device';
import {DeviceStatus} from '../model/DeviceStatus';
import {Observable, of} from 'rxjs';
import {Scannable} from '../model/Scannable';
import {Location} from '../model/Location';
import {Category} from '../model/Category';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {environment} from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class DeviceService {

    constructor(private http: HttpClient) {
    }

    getDevices(): Observable<Device[]> {
        return this.http.get<Device[]>(`${environment.apiUrl}/api/device`);
    }

    addDevice(device: Device): Observable<Device> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');
        device.id = null;

        return this.http.post<Device>(`${environment.apiUrl}/api/device/`, Device.toJsonString(device), {headers: headers});
    }

    updateDevice(device: Device): Observable<Device> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<Device>(`${environment.apiUrl}/api/device/`, Device.toJsonString(device), {headers: headers});
    }

    deleteDevice(device: Device): Observable<Device> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.delete<Device>(`${environment.apiUrl}/api/device/${device.id}`, {headers: headers});
    }
}
