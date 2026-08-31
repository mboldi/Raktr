export class ContainerUpdateDto {
  assetTag: string;
  barcode: string;
  name: string;
  weight: number;
  publicRentable: boolean;
  categoryName: string;
  locationName: string;
  ownerId: number;

  constructor(
    assetTag: string,
    barcode: string,
    name: string,
    weight: number,
    publicRentable: boolean,
    categoryName: string,
    locationName: string,
    ownerId: number
  ) {
    this.assetTag = assetTag;
    this.barcode = barcode;
    this.name = name;
    this.weight = weight;
    this.publicRentable = publicRentable;
    this.categoryName = categoryName;
    this.locationName = locationName;
    this.ownerId = ownerId;
  }

  toJson(): Record<string, unknown> {
    return {
      assetTag: this.assetTag,
      barcode: this.barcode,
      name: this.name,
      weight: this.weight,
      publicRentable: this.publicRentable,
      categoryName: this.categoryName,
      locationName: this.locationName,
      ownerId: this.ownerId
    };
  }
}
