import {UserDetails} from '../user/userDetails';
import {RentType} from './rentType';
import {RentItemDetailsDto} from './rentItem/rentItemDetails';
import {CommentDetailsDto} from '../comment/commentDetailsDto';

export class RentDetailsDto {
  id: number;
  type: RentType;
  destination: string;
  issuer: UserDetails;
  renterName: string;
  outDate: Date;
  expectedReturnDate: Date;
  actualReturnDate: Date;
  closed: boolean;
  deleted: boolean;
  rentItems: RentItemDetailsDto[];
  comments: CommentDetailsDto[];
  createdAt: Date;
  createdBy: UserDetails;
  updatedAt: Date;
  updatedBy: UserDetails;

  constructor(
    id: number,
    type: RentType,
    destination: string,
    issuer: UserDetails,
    renterName: string,
    outDate: Date,
    expectedReturnDate: Date,
    actualReturnDate: Date,
    closed: boolean,
    deleted: boolean,
    rentItems: RentItemDetailsDto[],
    comments: CommentDetailsDto[],
    createdAt: Date,
    createdBy: UserDetails,
    updatedAt: Date,
    updatedBy: UserDetails
  ) {
    this.id = id;
    this.type = type;
    this.destination = destination;
    this.issuer = issuer;
    this.renterName = renterName;
    this.outDate = outDate;
    this.expectedReturnDate = expectedReturnDate;
    this.actualReturnDate = actualReturnDate;
    this.closed = closed;
    this.deleted = deleted;
    this.rentItems = rentItems;
    this.comments = comments;
    this.createdAt = createdAt;
    this.createdBy = createdBy;
    this.updatedAt = updatedAt;
    this.updatedBy = updatedBy;
  }

  static fromJson(json: Record<string, unknown>): RentDetailsDto {
    return new RentDetailsDto(
      json['id'] as number,
      json['type'] as RentType,
      json['destination'] as string,
      UserDetails.fromJson(json['issuer'] as Record<string, unknown>),
      json['renterName'] as string,
      new Date(json['outDate'] as string),
      new Date(json['expectedReturnDate'] as string),
      new Date(json['actualReturnDate'] as string),
      json['closed'] as boolean,
      json['deleted'] as boolean,
      (json['rentItems'] as Record<string, unknown>[]).map(RentItemDetailsDto.fromJson),
      (json['comments'] as Record<string, unknown>[]).map(CommentDetailsDto.fromJson),
      new Date(json['createdAt'] as string),
      UserDetails.fromJson(json['createdBy'] as Record<string, unknown>),
      new Date(json['updatedAt'] as string),
      UserDetails.fromJson(json['updatedBy'] as Record<string, unknown>)
    );
  }
}
