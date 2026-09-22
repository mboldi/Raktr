import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { firstValueFrom } from 'rxjs';

import { AdminAccessService } from './adminAccess.service';
import { UserDetails } from '../model/user/userDetails';
import { environment } from '../../environments/environment';

function user(personalId: string): UserDetails {
  return new UserDetails('uuid', 'someone', 'Family', 'Given', 'nick', personalId, []);
}

describe('AdminAccessService', () => {
  let service: AdminAccessService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(AdminAccessService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
  });

  it('caches the user so it is only fetched once', async () => {
    const first = firstValueFrom(service.getCurrentUser());
    http
      .expectOne(`${environment.apiUrl}/v1/users/me`)
      .flush({ username: 'someone', personalId: 'AB123456' });
    expect((await first).personalId).toBe('AB123456');

    // the cached observable answers, so no second request is issued
    const second = await firstValueFrom(service.getCurrentUser());
    expect(second.personalId).toBe('AB123456');
    expect(http.match(`${environment.apiUrl}/v1/users/me`).length).toBe(0);
  });

  it('serves the updated user after setCurrentUser, without a reload', async () => {
    const initial = firstValueFrom(service.getCurrentUser());
    http
      .expectOne(`${environment.apiUrl}/v1/users/me`)
      .flush({ username: 'someone', personalId: '' });
    expect((await initial).personalId).toBe('');

    service.setCurrentUser(user('AB123456'));

    expect((await firstValueFrom(service.getCurrentUser())).personalId).toBe('AB123456');
    http.expectNone(`${environment.apiUrl}/v1/users/me`);
  });
});
