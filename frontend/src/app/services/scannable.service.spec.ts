import { TestBed } from '@angular/core/testing';

import { ScannableService } from './scannable.service';

describe('ScannableService', () => {
  let service: ScannableService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ScannableService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
