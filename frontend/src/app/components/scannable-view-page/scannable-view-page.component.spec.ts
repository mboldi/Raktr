import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScannableViewPageComponent } from './scannable-view-page.component';

describe('ScannableViewPageComponent', () => {
  let component: ScannableViewPageComponent;
  let fixture: ComponentFixture<ScannableViewPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScannableViewPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScannableViewPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
