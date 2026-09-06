import {Owner} from '../owner/owner';

export class Scannable {
  id: number;
  assetTag: string;
  name: string;
  owner: Owner;
  manufacturer: string;
  acquisitionSource: string;
  acquisitionDate: Date;
  warrantyEndDate: Date;

  protected constructor(
    id: number,
    assetTag: string,
    name: string,
    owner: Owner,
    manufacturer: string,
    acquisitionSource: string,
    acquisitionDate: Date,
    warrantyEndDate: Date
  ) {
    this.id = id;
    this.assetTag = assetTag;
    this.name = name;
    this.owner = owner;
    this.manufacturer = manufacturer;
    this.acquisitionSource = acquisitionSource;
    this.acquisitionDate = acquisitionDate;
    this.warrantyEndDate = warrantyEndDate;
  }

  static fromJson(json: Record<string, unknown>): Scannable {
    return new Scannable(
      json['id'] as number,
      json['assetTag'] as string,
      json['name'] as string,
      json['owner'] as Owner,
      json['manufacturer'] as string,
      json['acquisitionSource'] as string,
      new Date(json['acquisitionDate'] as string),
      new Date(json['warrantyEndDate'] as string)
    );
  }

  toJson(): Record<string, unknown> {
    console.log(this)
    return {
      id: this.id,
      assetTag: this.assetTag,
      name: this.name,
      owner: {
        id: this.owner.id,
        name: this.owner.name
      },
      manufacturer: this.manufacturer,
      acquisitionSource: this.acquisitionSource,
      acquisitionDate: this.acquisitionDate ? (this.acquisitionDate as Date).toISOString().split('T')[0] : "",
      warrantyEndDate: this.warrantyEndDate ? (this.warrantyEndDate as Date).toISOString().split('T')[0] : "",
    };
  }
}
