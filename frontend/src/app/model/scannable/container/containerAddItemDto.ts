export class ContainerAddItemDto {
  deviceId: number;
  quantity: number;

  constructor(deviceId: number, quantity: number) {
    this.deviceId = deviceId;
    this.quantity = quantity;
  }

  toJson(): Record<string, unknown> {
    return {
      deviceId: this.deviceId,
      quantity: this.quantity
    };
  }
}
