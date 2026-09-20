export class RentUpdateDto {
  destination: string;
  issuerId: string;
  renterName: string;
  outDate: Date;
  expectedReturnDate: Date;
  actualReturnDate: Date | null;

  constructor(
    destination: string,
    issuerId: string,
    renterName: string,
    outDate: Date,
    expectedReturnDate: Date,
    actualReturnDate: Date | null,
  ) {
    this.destination = destination;
    this.issuerId = issuerId;
    this.renterName = renterName;
    this.outDate = outDate;
    this.expectedReturnDate = expectedReturnDate;
    this.actualReturnDate = actualReturnDate;
  }

  static fromJson(json: Record<string, unknown>): RentUpdateDto {
    return new RentUpdateDto(
      json['destination'] as string,
      json['issuerId'] as string,
      json['renterName'] as string,
      new Date(json['outDate'] as string),
      new Date(json['expectedReturnDate'] as string),
      json['actualReturnDate'] ? new Date(json['actualReturnDate'] as string) : null,
    );
  }

  toJson(): Record<string, unknown> {
    return {
      destination: this.destination,
      issuerId: this.issuerId,
      renterName: this.renterName,
      outDate: this.outDate.toISOString().split('T')[0],
      expectedReturnDate: this.expectedReturnDate.toISOString().split('T')[0],
      actualReturnDate: this.actualReturnDate
        ? this.actualReturnDate.toISOString().split('T')[0]
        : null,
    };
  }
}
