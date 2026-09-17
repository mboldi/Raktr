import {ScannableDetailsDto} from '../../scannable/scannableDetailsDto';
import {RentItemStatus} from './rentItemStatus';
import {UserDetails} from '../../user/userDetails';

export class RentItemDetailsDto {
  id: number;
  scannable: ScannableDetailsDto;
  status: RentItemStatus;
  quantity: number;
  createdAt: Date;
  createdBy: UserDetails;
  updatedAt: Date;
  updatedBy: UserDetails;

  constructor(
    id: number,
    scannable: ScannableDetailsDto,
    status: RentItemStatus,
    quantity: number,
    createdAt: Date,
    createdBy: UserDetails,
    updatedAt: Date,
    updatedBy: UserDetails
  ) {
    this.id = id;
    this.scannable = scannable;
    this.status = status;
    this.quantity = quantity;
    this.createdAt = createdAt;
    this.createdBy = createdBy;
    this.updatedAt = updatedAt;
    this.updatedBy = updatedBy;
  }

  static fromJson(json: Record<string, unknown>): RentItemDetailsDto {
    return new RentItemDetailsDto(
      json['id'] as number,
      ScannableDetailsDto.fromJson(json['scannable'] as Record<string, unknown>),
      json['status'] as RentItemStatus,
      json['quantity'] as number,
      new Date(json['createdAt'] as string),
      UserDetails.fromJson(json['createdBy'] as Record<string, unknown>),
      new Date(json['updatedAt'] as string),
      UserDetails.fromJson(json['updatedBy'] as Record<string, unknown>)
    );
  }
}
