import {UserDetails} from '../user/userDetails';

export class LocationDetails {
  name: string;
  createdAt: Date;
  createdBy: UserDetails;
  updatedAt: Date;
  updatedBy: UserDetails;

  constructor(name: string, createdAt: Date, createdBy: UserDetails, updatedAt: Date, updatedBy: UserDetails) {
    this.name = name;
    this.createdAt = createdAt;
    this.createdBy = createdBy;
    this.updatedAt = updatedAt;
    this.updatedBy = updatedBy;
  }

  static fromJson(json: Record<string, unknown>): LocationDetails {
    return new LocationDetails(
      json['name'] as string,
      new Date(json['createdAt'] as string),
      UserDetails.fromJson(json['createdBy'] as Record<string, unknown>),
      new Date(json['updatedAt'] as string),
      UserDetails.fromJson(json['updatedBy'] as Record<string, unknown>)
    );
  }
}
