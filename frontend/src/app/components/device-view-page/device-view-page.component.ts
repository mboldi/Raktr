import { Component, Input, ChangeDetectionStrategy, OnInit, inject } from '@angular/core';
import { DeviceStatus } from '../../model/scannable/device/deviceStatus';
import { DeviceDetails } from '../../model/scannable/device/deviceDetails';
import { MatIcon } from '@angular/material/icon';
import { DatePipe, DecimalPipe, NgClass } from '@angular/common';
import { MatChip } from '@angular/material/chips';
import { MatDivider } from '@angular/material/list';
import { MatCard, MatCardContent } from '@angular/material/card';
import { ContainerDetails } from '../../model/scannable/container/containerDetails';
import { DeviceService } from '../../services/device.service';

@Component({
  selector: 'app-device-view-page',
  imports: [MatIcon, DatePipe, DecimalPipe, NgClass, MatChip, MatDivider, MatCardContent, MatCard],
  templateUrl: './device-view-page.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './device-view-page.component.scss',
})
export class DeviceViewPageComponent implements OnInit {
  private deviceService = inject(DeviceService);

  @Input({ required: true }) device!: DeviceDetails;

  protected containers: ContainerDetails[] = [];

  ngOnInit() {
    this.deviceService
      .getContainersOfDevice(this.device.id)
      .subscribe((containers) => (this.containers = containers));
  }

  getStatusLabel(status: DeviceStatus): string {
    switch (status) {
      case DeviceStatus.GOOD:
        return 'Jó';
      case DeviceStatus.NEEDS_REPAIR:
        return 'Javítást igényel';
      case DeviceStatus.SCRAPPED:
        return 'Selejt';
      default:
        return '';
    }
  }
}
