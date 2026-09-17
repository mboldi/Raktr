import {RentItemStatus} from './rentItemStatus';

export class RentItemUpdateDto {
  status: RentItemStatus;
  quantity: number;

  constructor(status: RentItemStatus, quantity: number) {
    this.status = status;
    this.quantity = quantity;
  }

  toJson(): Record<string, unknown> {
    return {
      status: this.status,
      quantity: this.quantity
    };
  }
}
