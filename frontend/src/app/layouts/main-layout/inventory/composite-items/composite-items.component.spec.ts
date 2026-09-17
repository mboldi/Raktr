import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompositeItemsComponent } from './composite-items.component';

describe('CompositeItemsComponent', () => {
  let component: CompositeItemsComponent;
  let fixture: ComponentFixture<CompositeItemsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompositeItemsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CompositeItemsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
