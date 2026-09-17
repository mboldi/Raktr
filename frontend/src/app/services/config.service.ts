import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {ConfigDetailsDto} from '../model/config/configDetailsDto';
import {ConfigUpdateDto} from '../model/config/configUpdateDto';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  constructor(private http: HttpClient) {
  }

  getConfigs(): Observable<ConfigDetailsDto[]> {
    return this.http.get<Record<string, unknown>[]>(`${environment.apiUrl}/v1/configs`)
      .pipe(
        map(categories => {
          const configDetailsDtos: ConfigDetailsDto[] = [];

          categories.forEach(config => configDetailsDtos.push(ConfigDetailsDto.fromJson(config)));

          return configDetailsDtos;
        })
      )
  }

  updateConfig(key: string, valueUpdate: ConfigUpdateDto): Observable<ConfigDetailsDto> {
    return this.http.put<Record<string, unknown>>(
      `${environment.apiUrl}/v1/configs/${key}`,
      valueUpdate
    ).pipe(
      map(json => ConfigDetailsDto.fromJson(json))
    );
  }


}
