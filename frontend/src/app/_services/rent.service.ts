import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {Rent} from '../model/Rent';
import {RentItem} from '../model/RentItem';
import {Comment} from '../model/Comment';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {saveAs} from 'file-saver';
import {map} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class RentService {

    constructor(private http: HttpClient) {
    }

    getRents(): Observable<Rent[]> {
        return this.http.get<Rent[]>(`${environment.apiUrl}/api/rent`)
            .pipe(
                map(rents => {
                    const rents_typed: Rent[] = [];

                    rents.forEach(rent => rents_typed.push(Rent.fromJson(rent)))

                    return rents_typed;
                })
            );
    }

    addRent(rent: Rent): Observable<Rent> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.post<Rent>(`${environment.apiUrl}/api/rent`, Rent.toJsonString(rent), {headers: headers})
            .pipe(
                map(rent_ => Rent.fromJson(rent_))
            );
    }

    updateRent(rent: Rent): Observable<Rent> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<Rent>(`${environment.apiUrl}/api/rent`, Rent.toJsonString(rent), {headers: headers})
            .pipe(
                map(rent_ => Rent.fromJson(rent_))
            );
    }

    finalizeRent(rent: Rent): Observable<Rent> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<Rent>(`${environment.apiUrl}/api/rent/finalize`, Rent.toJsonString(rent), {headers: headers})
            .pipe(
                map(rent_ => Rent.fromJson(rent_))
            );
    }

    getRent(id: number | string): Observable<Rent> {
        return this.http.get<Rent>(`${environment.apiUrl}/api/rent/${id}`)
            .pipe(
                map(rent_ => Rent.fromJson(rent_))
            );
    }

    updateInRent(rentId: number, rentItem: RentItem) {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<Rent>(`${environment.apiUrl}/api/rent/${rentId}`,
            RentItem.toJson(rentItem),
            {headers: headers})
            .pipe(
                map(rent_ => Rent.fromJson(rent_))
            );
    }

    addItemToRent(rentId: number, newRentItem: RentItem): Observable<Rent> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<Rent>(`${environment.apiUrl}/api/rent/${rentId}`,
            RentItem.toJson(newRentItem),
            {headers: headers})
            .pipe(
                map(rent_ => Rent.fromJson(rent_))
            );
    }

    deleteRent(rent: Rent): Observable<Rent> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.request<Rent>('delete', `${environment.apiUrl}/api/rent`,
            {headers: headers, body: Rent.toJsonString(rent)})
            .pipe(
                map(rent_ => Rent.fromJson(rent_))
            );
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

    addComment(rent: Rent, comment: Comment): Observable<Rent> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.post<Rent>(`${environment.apiUrl}/api/rent/${rent.id}/comment`, comment.toJson(), {headers: headers})
            .pipe(
                map(rent_ => Rent.fromJson(rent_))
            );
    }

    deleteComment(rent: Rent, comment: Comment): Observable<Rent> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.request<Rent>('delete', `${environment.apiUrl}/api/rent/${rent.id}/comment`, {
                headers: headers,
                body: comment.toJson()
            })
            .pipe(
                map(rent_ => Rent.fromJson(rent_))
            );
    }
}
