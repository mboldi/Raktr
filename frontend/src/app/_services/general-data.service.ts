import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {GeneralData} from '../_model/GeneralData';
import {environment} from '../../environments/environment';
import {map} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class GeneralDataService {

    constructor(private http: HttpClient) {
    }

    getAll(): Observable<GeneralData[]> {
        return this.http.get<GeneralData[]>(`${environment.apiUrl}/api/generaldata`)
            .pipe(
                map(datas => {
                    const datas_typed: GeneralData[] = [];

                    datas.forEach(data => datas_typed.push(GeneralData.fromJson(data)));

                    return datas_typed
                })
            );
    }

    getByKey(key: string): Observable<GeneralData> {
        return this.http.get<GeneralData>(`${environment.apiUrl}/api/generaldata/${key}`);
    }

    updateData(generalData: GeneralData): Observable<GeneralData> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<GeneralData>(`${environment.apiUrl}/api/generaldata/`,
            GeneralData.toJsonString(generalData), {headers: headers})
            .pipe(
                map(data => GeneralData.fromJson(data))
            );
    }
}
