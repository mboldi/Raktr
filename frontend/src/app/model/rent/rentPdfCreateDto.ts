export class RentPdfCreateDto {
  renterName: string;
  renterId: string;

  constructor(renterName: string, renterId: string) {
    this.renterName = renterName;
    this.renterId = renterId;
  }
}
