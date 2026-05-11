import {DeviceStatus} from './deviceStatus';
import {Owner} from '../../owner/owner';
import {Scannable} from '../scannable';

export class DeviceUpdateDto extends Scannable {
  barcode: string;
  categoryName: string;
  locationName: string;
  ownerId: number;
  model: string;
  serialNumber: string;
  estimatedValue: number;
  status: DeviceStatus;
  quantity: number;
  publicRentable: boolean;
  notes: string;

  constructor(
    id: number,
    assetTag: string,
    name: string,
    owner: Owner,
    manufacturer: string,
    acquisitionSource: string,
    acquisitionDate: Date,
    warrantyEndDate: Date,
    barcode: string,
    categoryName: string,
    locationName: string,
    ownerId: number,
    model: string,
    serialNumber: string,
    estimatedValue: number,
    status: DeviceStatus,
    quantity: number,
    publicRentable: boolean,
    notes: string
  ) {
    super(id, assetTag, name, owner, manufacturer, acquisitionSource, acquisitionDate, warrantyEndDate);
    this.barcode = barcode;
    this.categoryName = categoryName;
    this.locationName = locationName;
    this.ownerId = ownerId;
    this.model = model;
    this.serialNumber = serialNumber;
    this.estimatedValue = estimatedValue;
    this.status = status;
    this.quantity = quantity;
    this.publicRentable = publicRentable;
    this.notes = notes;
  }

  static override fromJson(json: Record<string, unknown>): DeviceUpdateDto {
    return new DeviceUpdateDto(
      json['id'] as number,
      json['assetTag'] as string,
      json['name'] as string,
      json['owner'] as Owner,
      json['manufacturer'] as string,
      json['acquisitionSource'] as string,
      new Date(json['acquisitionDate'] as string),
      new Date(json['warrantyEndDate'] as string),
      json['barcode'] as string,
      json['categoryName'] as string,
      json['locationName'] as string,
      json['ownerId'] as number,
      json['model'] as string,
      json['serialNumber'] as string,
      json['estimatedValue'] as number,
      json['status'] as DeviceStatus,
      json['quantity'] as number,
      json['publicRentable'] as boolean,
      json['notes'] as string
    );
  }

  override toJson(): Record<string, unknown> {
    return {
      ...super.toJson(),
      barcode: this.barcode,
      categoryName: this.categoryName,
      locationName: this.locationName,
      ownerId: this.ownerId,
      model: this.model,
      serialNumber: this.serialNumber,
      estimatedValue: this.estimatedValue,
      status: this.status,
      quantity: this.quantity,
      publicRentable: this.publicRentable,
      acquisitionDate: this.acquisitionDate.toISOString().split('T')[0],
      warrantyEndDate: this.warrantyEndDate.toISOString().split('T')[0],
      notes: this.notes
    };
  }
}
