import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {GeneralData} from '../model/GeneralData';
import {environment} from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class GeneralDataService {

    constructor(private http: HttpClient) {
    }

    getAll(): Observable<GeneralData[]> {
        return this.http.get<GeneralData[]>(`${environment.apiUrl}/api/generaldata`);
    }

    updateData(generalData: GeneralData): Observable<GeneralData> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<GeneralData>(`${environment.apiUrl}/api/generaldata/`,
            GeneralData.toJsonString(generalData), {headers: headers});
    }
}
