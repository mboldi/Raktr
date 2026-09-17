import { TestBed } from '@angular/core/testing';

import { CompositeService } from './composite.service';

describe('CompositeServiceService', () => {
  let service: CompositeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CompositeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
