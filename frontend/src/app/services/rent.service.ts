import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Rent} from '../model/Rent';
import {RentItem} from '../model/RentItem';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {saveAs} from 'file-saver';

@Injectable({
    providedIn: 'root'
})
export class RentService {

    constructor(private http: HttpClient) {
    }

    getRents(): Observable<Rent[]> {
        return this.http.get<Rent[]>(`${environment.apiUrl}/api/rent`);
    }

    addRent(rent: Rent): Observable<Rent> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.post<Rent>(`${environment.apiUrl}/api/rent`, Rent.toJsonString(rent), {headers: headers});
    }

    updateRent(rent: Rent): Observable<Rent> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<Rent>(`${environment.apiUrl}/api/rent`, Rent.toJsonString(rent), {headers: headers});
    }

    getRent(id: number | string): Observable<Rent> {
        return this.http.get<Rent>(`${environment.apiUrl}/api/rent/${id}`);
    }

    updateInRent(rentId: number, rentItem: RentItem) {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<Rent>(`${environment.apiUrl}/api/rent/${rentId}`,
            RentItem.toJson(rentItem),
            {headers: headers})
    }

    addItemToRent(rentId: number, newRentItem: RentItem): Observable<Rent> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<Rent>(`${environment.apiUrl}/api/rent/${rentId}`,
            RentItem.toJson(newRentItem),
            {headers: headers})
    }

    deleteRent(rent: Rent): Observable<Rent> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.request<Rent>('delete', `${environment.apiUrl}/api/rent`,
            {headers: headers, body: Rent.toJsonString(rent)});
    }

    getPdf(rent: Rent, pdfRequest: string) {
        const mediaType = 'application/pdf';
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        this.http.post(`${environment.apiUrl}/api/rent/pdf/${rent.id}`, pdfRequest,
            {headers: headers, responseType: 'blob'}).subscribe(res => {
                const blob = new Blob([res], {type: mediaType});
                saveAs(blob, `${rent.destination}_szallitolevel.pdf`);
            },
            error => {
            })
    }
}
