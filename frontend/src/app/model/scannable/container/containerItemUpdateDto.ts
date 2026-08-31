export class ContainerItemUpdateDto {
  quantity: number;

  constructor(quantity: number) {
    this.quantity = quantity;
  }

  toJson(): Record<string, unknown> {
    return {
      quantity: this.quantity
    };
  }
}
