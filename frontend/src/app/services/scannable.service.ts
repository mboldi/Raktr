import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, map, Observable, of} from 'rxjs';
import {environment} from '../../environments/environment';
import {ScannableDetailsDto} from '../model/scannable/scannableDetailsDto';
import {DeviceDetails} from '../model/scannable/device/deviceDetails';

@Injectable({
  providedIn: 'root'
})
export class ScannableService {

  constructor(private http: HttpClient) {
  }

  getScannablesCount(): Observable<number> {
    return this.http.get<number>(`${environment.apiUrl}/v1/scannables/count`)
      .pipe(
        map(count => {
          return count;
        })
      )
  }

  isBarcodeTaken(barcodeToCheck: string): Observable<boolean> {
    return this.http.head(`${environment.apiUrl}/v1/scannables/barcode/${barcodeToCheck}`, { observe: 'response' })
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

  isAssetTagTaken(assetTagToCheck: string): Observable<boolean> {
    return this.http.head(`${environment.apiUrl}/v1/scannables/assetTag/${assetTagToCheck}`, { observe: 'response' })
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

  getByBarcode(barcode: string): Observable<ScannableDetailsDto> {
    return this.http.get<Record<string, unknown>>(`${environment.apiUrl}/v1/scannables/barcode/${barcode}`)
      .pipe(
        map(device => {
          console.log(device)
          return DeviceDetails.fromJson(device);
        })
      )
  }

}
