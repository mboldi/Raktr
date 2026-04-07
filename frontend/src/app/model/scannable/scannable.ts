import {Owner} from '../owner/owner';

export abstract class Scannable {
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
    throw new Error('fromJson() must be implemented by subclass');
  }

  toJson(): Record<string, unknown> {
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
      acquisitionDate: this.acquisitionDate.toISOString(),
      warrantyEndDate: this.warrantyEndDate.toISOString()
    };
  }
}
