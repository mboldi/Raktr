import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeviceToRentModalComponent } from './device-to-rent-modal.component';

describe('DeviceToRentModalComponent', () => {
  let component: DeviceToRentModalComponent;
  let fixture: ComponentFixture<DeviceToRentModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeviceToRentModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeviceToRentModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
