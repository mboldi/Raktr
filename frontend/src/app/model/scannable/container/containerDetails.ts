import {ScannableDetailsDto} from '../scannableDetailsDto';
import {Owner} from '../../owner/owner';
import {UserDetails} from '../../user/userDetails';
import {ContainerItemDetails} from './containerItemDetails';

export class ContainerDetails extends ScannableDetailsDto {
  totalWeight: number;
  items: ContainerItemDetails[];

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
    updatedBy: UserDetails,
    totalWeight: number,
    items: ContainerItemDetails[]
  ) {
    super(id, assetTag, barcode, name, weight, publicRentable, deleted, category, location, owner, createdAt, createdBy, updatedAt, updatedBy);
    this.totalWeight = totalWeight;
    this.items = items;
  }

  public getItemCount(): number {
    return this.items.reduce((sum, item) => sum + item.quantity, 0);
  }

  static override fromJson(json: Record<string, unknown>): ContainerDetails {
    return new ContainerDetails(
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
      UserDetails.fromJson(json['updatedBy'] as Record<string, unknown>),
      json['totalWeight'] as number,
      Array.isArray(json['items'])
        ? (json['items'] as Record<string, unknown>[]).map(item => ContainerItemDetails.fromJson(item))
        : []
    );
  }
}
