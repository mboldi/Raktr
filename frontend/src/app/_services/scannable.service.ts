import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Observable} from 'rxjs';
import {Scannable} from '../model/Scannable';
import {map} from 'rxjs/operators';
import {Device} from '../model/Device';
import {CompositeItem} from '../model/CompositeItem';

@Injectable({
    providedIn: 'root'
})
export class ScannableService {

    constructor(private http: HttpClient) {
    }

    getNextId(): Observable<number> {
        return this.http.get<number>(`${environment.apiUrl}/api/scannable/nextid`);
    }

    // @ts-ignore
    getScannableByBarcode(barcode: string): Observable<Scannable> {
        return this.http.get(`${environment.apiUrl}/api/scannable/barcode/${barcode}`, {observe: 'response'})
            .pipe(
                map(res => {
                    if (res.status === 404) {
                        return undefined;
                    } else {
                        if (res.body['@type'] === 'device') {
                            return Device.fromJson(res.body as Device);
                        } else if (res.body['@type'] === 'compositeItem') {
                            return CompositeItem.fromJson(res.body as CompositeItem);
                        }
                    }
                })
            )
    }

    getScannableByTextIdentifier(textIdentifier: string): Observable<Scannable> {
        // @ts-ignore
        return this.http.get(`${environment.apiUrl}/api/scannable/textid/${textIdentifier}`, {observe: 'response'})
            .pipe(
                map(res => {
                    if (res.status === 404) {
                        return undefined;
                    } else {
                        if (res.body['@type'] === 'device') {
                            return Device.fromJson(res.body as Device);
                        } else if (res.body['@type'] === 'compositeItem') {
                            return CompositeItem.fromJson(res.body as CompositeItem);
                        }
                    }
                })
            )
    }

    getScannableAmount(): Observable<number> {
        return this.http.get<number>(`${environment.apiUrl}/api/scannable/count`);
    }

    getTakenByTextId(textId: string): Observable<number> {
        return this.http.get<number>(`${environment.apiUrl}/api/scannable/textidtaken/${textId}`);
    }

    getTakenByBarcode(barcode: string): Observable<number> {
        return this.http.get<number>(`${environment.apiUrl}/api/scannable/barcodetaken/${barcode}`);
    }

}
