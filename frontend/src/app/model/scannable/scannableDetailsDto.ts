import {Owner} from '../owner/owner';
import {UserDetails} from '../user/userDetails';

export class ScannableDetailsDto {
  id: number;
  assetTag: string;
  barcode: string;
  name: string;
  weight: number;
  publicRentable: boolean;
  deleted: boolean;
  category: string;
  location: string;
  owner: Owner;
  createdAt: Date;
  createdBy: UserDetails;
  updatedAt: Date;
  updatedBy: UserDetails;

  constructor(
    id: number,
    assetTag: string,
    barcode: string,
    name: string,
    weight: number,
    publicRentable: boolean,
    deleted: boolean,
    category: string,
    location: string,
    owner: Owner,
    createdAt: Date,
    createdBy: UserDetails,
    updatedAt: Date,
    updatedBy: UserDetails
  ) {
    this.id = id;
    this.assetTag = assetTag;
    this.barcode = barcode;
    this.name = name;
    this.weight = weight;
    this.publicRentable = publicRentable;
    this.deleted = deleted;
    this.category = category;
    this.location = location;
    this.owner = owner;
    this.createdAt = createdAt;
    this.createdBy = createdBy;
    this.updatedAt = updatedAt;
    this.updatedBy = updatedBy;
  }

  static fromJson(json: Record<string, unknown>): ScannableDetailsDto {
    return new ScannableDetailsDto(
      json['id'] as number,
      json['assetTag'] as string,
      json['barcode'] as string,
      json['name'] as string,
      json['weight'] as number,
      json['publicRentable'] as boolean,
      json['deleted'] as boolean,
      json['category'] as string,
      json['location'] as string,
      json['owner'] as Owner,
      new Date(json['createdAt'] as string),
      UserDetails.fromJson(json['createdBy'] as Record<string, unknown>),
      new Date(json['updatedAt'] as string),
      UserDetails.fromJson(json['updatedBy'] as Record<string, unknown>)
    );
  }
}
