import {UserDetails} from '../user/userDetails';

export class OwnerDetailsDto {
  id: number;
  name: string;
  inSchInventory: boolean;
  createdAt: Date;
  createdBy: UserDetails;
  updatedAt: Date;
  updatedBy: UserDetails;

  constructor(
    id: number,
    name: string,
    inSchInventory: boolean,
    createdAt: Date,
    createdBy: UserDetails,
    updatedAt: Date,
    updatedBy: UserDetails
  ) {
    this.id = id;
    this.name = name;
    this.inSchInventory = inSchInventory;
    this.createdAt = createdAt;
    this.createdBy = createdBy;
    this.updatedAt = updatedAt;
    this.updatedBy = updatedBy;
  }

  static fromJson(json: Record<string, unknown>): OwnerDetailsDto {
    return new OwnerDetailsDto(
      json['id'] as number,
      json['name'] as string,
      json['inSchInventory'] as boolean,
      new Date(json['createdAt'] as string),
      UserDetails.fromJson(json['createdBy'] as Record<string, unknown>),
      new Date(json['updatedAt'] as string),
      UserDetails.fromJson(json['updatedBy'] as Record<string, unknown>)
    );
  }
}
