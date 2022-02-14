import {Injectable} from '@angular/core';
import {CompositeItem} from '../model/CompositeItem';
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Device} from '../model/Device';

@Injectable({
    providedIn: 'root'
})
export class CompositeService {

    constructor(private http: HttpClient) {
    }

    getCompositeItems(): Observable<CompositeItem[]> {
        return this.http.get<CompositeItem[]>(`${environment.apiUrl}/api/composite`);
    }

    addCompositeItem(compositeItem: CompositeItem): Observable<CompositeItem> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');
        compositeItem.id = null;

        return this.http.post<CompositeItem>(`${environment.apiUrl}/api/composite`, CompositeItem.toJsonString(compositeItem),
            {headers: headers}).pipe();
    }

    updateCompositeItem(compositeItem: CompositeItem): Observable<CompositeItem> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<CompositeItem>(`${environment.apiUrl}/api/composite`, CompositeItem.toJsonString(compositeItem),
            {headers: headers});
    }

    addDeviceToComposite(device: Device, compositeId: number): Observable<CompositeItem> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.put<CompositeItem>(`${environment.apiUrl}/api/composite/${compositeId}`, Device.toJsonString(device),
            {headers: headers});
    }

    removeDeviceFromComposite(device: Device, compositeId: number) {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.request<CompositeItem>('delete', `${environment.apiUrl}/api/composite/${compositeId}`,
            {body: Device.toJsonString(device), headers: headers});
    }

    getCompositeItemById(id: number): Observable<CompositeItem> {
        return this.http.get<CompositeItem>(`${environment.apiUrl}/api/composite/${id}`);
    }

    deleteComposite(compositeItem: CompositeItem): Observable<CompositeItem> {
        const headers = new HttpHeaders().set('Content-Type', 'application/json');

        return this.http.request<CompositeItem>('delete',
            `${environment.apiUrl}/api/composite`,
            {body: CompositeItem.toJsonString(compositeItem), headers: headers});
    }
}
