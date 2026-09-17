export class RentPdfCreateDto {
  renterId: string;

  constructor(renterId: string) {
    this.renterId = renterId;
  }
}
