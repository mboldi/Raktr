import { RentType } from './rentType';

export class RentCreateDto {
  type: RentType;
  destination: string;
  issuerId: string;
  renterName: string;
  outDate: Date;
  expectedReturnDate: Date;

  constructor(
    type: RentType,
    destination: string,
    issuerId: string,
    renterName: string,
    outDate: Date,
    expectedReturnDate: Date,
  ) {
    this.type = type;
    this.destination = destination;
    this.issuerId = issuerId;
    this.renterName = renterName;
    this.outDate = outDate;
    this.expectedReturnDate = expectedReturnDate;
  }

  toJson(): Record<string, unknown> {
    return {
      type: this.type,
      destination: this.destination,
      issuerId: this.issuerId,
      renterName: this.renterName,
      outDate: this.outDate.toISOString().split('T')[0],
      expectedReturnDate: this.expectedReturnDate.toISOString().split('T')[0],
    };
  }
}
