import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {Category} from '../model/category/category';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  constructor(private http: HttpClient) {
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/categories`)
      .pipe(
        map(categories => {
          const categories_typed: Category[] = [];

          categories.forEach(category => categories_typed.push(Category.fromJson(category)));

          console.log(categories);

          return categories_typed;
        })
      )
  }
}
