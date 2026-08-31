import {DeviceDetails} from '../device/deviceDetails';

export class ContainerItemDetails {
  device: DeviceDetails;
  quantity: number;

  constructor(device: DeviceDetails, quantity: number) {
    this.device = device;
    this.quantity = quantity;
  }

  static fromJson(json: Record<string, unknown>): ContainerItemDetails {
    return new ContainerItemDetails(
      DeviceDetails.fromJson(json['device'] as Record<string, unknown>),
      json['quantity'] as number
    );
  }
}
