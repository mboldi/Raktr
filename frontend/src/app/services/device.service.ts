import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {DeviceDetails} from "../model/scannable/device/DeviceDetails";

@Injectable({
  providedIn: 'root'
})
export class DeviceService {

  constructor(private http: HttpClient) {
  }
  
  public getDevices(): Observable<DeviceDetails[]> {
      return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/devices`)
          .pipe(
              map(categories => {
                  const devices_typed: DeviceDetails[] = [];

                  categories.forEach(device => devices_typed.push(DeviceDetails.fromJson(device)));

                  return devices_typed;
              })
          )
  }

}
