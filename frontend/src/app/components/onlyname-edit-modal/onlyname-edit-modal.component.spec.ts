import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnlynameEditModalComponent } from './onlyname-edit-modal.component';

describe('OnlynameEditModalComponent', () => {
  let component: OnlynameEditModalComponent;
  let fixture: ComponentFixture<OnlynameEditModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OnlynameEditModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OnlynameEditModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
