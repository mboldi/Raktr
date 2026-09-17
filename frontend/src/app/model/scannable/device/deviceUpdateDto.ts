import {DeviceStatus} from './deviceStatus';

export class DeviceUpdateDto {
  assetTag: string;
  barcode: string;
  name: string;
  weight: number;
  publicRentable: boolean;
  categoryName: string;
  locationName: string;
  ownerId: number;
  manufacturer: string;
  model: string;
  serialNumber: string;
  estimatedValue: number;
  status: DeviceStatus;
  quantity: number;
  acquisitionSource: string;
  acquisitionDate: Date;
  warrantyEndDate: Date;
  notes: string;

  constructor(
    assetTag: string,
    barcode: string,
    name: string,
    weight: number,
    publicRentable: boolean,
    categoryName: string,
    locationName: string,
    ownerId: number,
    manufacturer: string,
    model: string,
    serialNumber: string,
    estimatedValue: number,
    status: DeviceStatus,
    quantity: number,
    acquisitionSource: string,
    acquisitionDate: Date,
    warrantyEndDate: Date,
    notes: string
  ) {
    this.assetTag = assetTag;
    this.barcode = barcode;
    this.name = name;
    this.weight = weight;
    this.publicRentable = publicRentable;
    this.categoryName = categoryName;
    this.locationName = locationName;
    this.ownerId = ownerId;
    this.manufacturer = manufacturer;
    this.model = model;
    this.serialNumber = serialNumber;
    this.estimatedValue = estimatedValue;
    this.status = status;
    this.quantity = quantity;
    this.acquisitionSource = acquisitionSource;
    this.acquisitionDate = new Date(acquisitionDate);
    this.warrantyEndDate = new Date(warrantyEndDate);
    this.notes = notes;
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
      ownerId: this.ownerId,
      manufacturer: this.manufacturer,
      model: this.model,
      serialNumber: this.serialNumber,
      estimatedValue: this.estimatedValue,
      status: this.status,
      quantity: this.quantity,
      acquisitionSource: this.acquisitionSource,
      acquisitionDate: this.acquisitionDate.toISOString().split('T')[0],
      warrantyEndDate: this.warrantyEndDate.toISOString().split('T')[0],
      notes: this.notes
    };
  }

  static fromFormControl(formValue: any) {

    return new DeviceUpdateDto(
      formValue.assetTag,
      formValue.barcode,
      formValue.name,
      formValue.weight,
      formValue.isPublicRentable,
      formValue.category,
      formValue.location,
      formValue.owner.id,
      formValue.manufacturer,
      formValue.model,
      formValue.serialNumber,
      formValue.estimatedValue,
      formValue.status,
      formValue.quantity,
      formValue.acquisitionSource,
      new Date(formValue.acquisitionDate),
      new Date(formValue.warrantyEndDate),
      formValue.notes
    )
  }
}
