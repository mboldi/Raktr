import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SignersComponent } from './signers.component';

describe('SignersComponent', () => {
  let component: SignersComponent;
  let fixture: ComponentFixture<SignersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SignersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SignersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
