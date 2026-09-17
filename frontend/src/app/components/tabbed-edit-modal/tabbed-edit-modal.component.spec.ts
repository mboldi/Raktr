import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TabbedEditModalComponent } from './tabbed-edit-modal.component';

describe('TabbedEditModalComponent', () => {
  let component: TabbedEditModalComponent;
  let fixture: ComponentFixture<TabbedEditModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TabbedEditModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TabbedEditModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
