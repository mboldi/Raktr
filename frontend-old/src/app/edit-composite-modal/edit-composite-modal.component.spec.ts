import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditCompositeModalComponent } from './edit-composite-modal.component';

describe('EditCompositeModalComponent', () => {
  let component: EditCompositeModalComponent;
  let fixture: ComponentFixture<EditCompositeModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditCompositeModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditCompositeModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
