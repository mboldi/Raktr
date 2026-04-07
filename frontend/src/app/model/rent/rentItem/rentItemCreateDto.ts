export class RentItemCreateDto {
  public scannableId: number;
  public quantity: number;

  constructor(scannableId: number, quantity: number) {
    this.scannableId = scannableId;
    this.quantity = quantity;
  }
}
