import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeviceEditDialogComponent } from './device-edit-dialog.component';

describe('DeviceEditModalComponent', () => {
  let component: DeviceEditDialogComponent;
  let fixture: ComponentFixture<DeviceEditDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeviceEditDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeviceEditDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
