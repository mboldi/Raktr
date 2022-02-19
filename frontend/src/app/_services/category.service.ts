import {Injectable} from '@angular/core';
import {Category} from '../model/Category';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {map} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class CategoryService {

    constructor(private http: HttpClient) {
    }

    getCategories(): Observable<Category[]> {
        return this.http.get<Category[]>(`${environment.apiUrl}/api/category`)
            .pipe(
                map(categories => {
                    const categories_typed: Category[] = [];

                    categories.forEach(category => categories_typed.push(Category.fromJson(category)));

                    return categories_typed;
                })
            )
    }
}
