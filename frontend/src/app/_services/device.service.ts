import {Injectable} from '@angular/core';
import {Device} from '../model/Device';
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {map} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class DeviceService {

    constructor(private http: HttpClient) {
    }

    getDevices(): Observable<Device[]> {
        return this.http.get<Device[]>(`${environment.apiUrl}/api/device`)
            .pipe(
                map(devices => {
                    const devices_typed: Device[] = [];

                    devices.forEach(device => devices_typed.push(Device.fromJson(device)));

                    return devices_typed;
                })
            );
    }

    addDevice(device: Device): Observable<Device> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');
        device.id = null;

        return this.http.post<Device>(`${environment.apiUrl}/api/device/`, Device.toJsonString(device), {headers: headers})
            .pipe(
                map(gotDevice => Device.fromJson(gotDevice))
            );
    }

    updateDevice(device: Device): Observable<Device> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<Device>(`${environment.apiUrl}/api/device/`, Device.toJsonString(device), {headers: headers})
            .pipe(
                map(gotDevice => Device.fromJson(gotDevice))
            );
    }

    deleteDevice(device: Device): Observable<Device> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.delete<Device>(`${environment.apiUrl}/api/device/${device.id}`, {headers: headers})
            .pipe(
                map(gotDevice => Device.fromJson(gotDevice))
            );
    }
}
