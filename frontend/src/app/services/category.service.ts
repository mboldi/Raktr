import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {CategoryDetails} from '../model/category/categoryDetails';
import {environment} from '../../environments/environment';
import {CategoryCreateDto} from '../model/category/categoryCreateDto';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  constructor(private http: HttpClient) {
  }

  getCategories(): Observable<CategoryDetails[]> {
    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/categories`)
      .pipe(
        map(categories => {
          const categories_typed: CategoryDetails[] = [];

          categories.forEach(category => categories_typed.push(CategoryDetails.fromJson(category)));

          return categories_typed;
        })
      )
  }

  addCategory(name: string): Observable<CategoryDetails> {
    const newCategory = new CategoryCreateDto(name);

    return this.http.post<Record<string, unknown>>(
      `${environment.apiUrl}/v1/categories`,
      newCategory
    ).pipe(
      map(json => CategoryDetails.fromJson(json))
    );
  }

  deleteCategory(name: string) {
    return this.http.delete(`${environment.apiUrl}/v1/categories/${name}`);
  }


}
