import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeviceViewPageComponent } from './device-view-page.component';

describe('DeviceViewPageComponent', () => {
  let component: DeviceViewPageComponent;
  let fixture: ComponentFixture<DeviceViewPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeviceViewPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeviceViewPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
