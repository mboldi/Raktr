import { Owner } from '../owner/owner';
import { dateFromJson, dateToJson } from '../jsonDate';

export class Scannable {
  id: number;
  assetTag: string;
  name: string;
  owner: Owner;
  manufacturer: string;
  acquisitionSource: string;
  acquisitionDate: Date | null;
  warrantyEndDate: Date | null;

  protected constructor(
    id: number,
    assetTag: string,
    name: string,
    owner: Owner,
    manufacturer: string,
    acquisitionSource: string,
    acquisitionDate: Date | null,
    warrantyEndDate: Date | null,
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
      dateFromJson(json['acquisitionDate']),
      dateFromJson(json['warrantyEndDate']),
    );
  }

  toJson(): Record<string, unknown> {
    return {
      id: this.id,
      assetTag: this.assetTag,
      name: this.name,
      owner: {
        id: this.owner.id,
        name: this.owner.name,
      },
      manufacturer: this.manufacturer,
      acquisitionSource: this.acquisitionSource,
      acquisitionDate: dateToJson(this.acquisitionDate),
      warrantyEndDate: dateToJson(this.warrantyEndDate),
    };
  }
}
