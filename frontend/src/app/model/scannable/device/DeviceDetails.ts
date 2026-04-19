import {DeviceStatus} from "./deviceStatus";
import {ScannableDetailsDto} from "../scannableDetailsDto";
import {Owner} from "../../owner/owner";
import {UserDetails} from "../../user/userDetails";

export class DeviceDetails extends ScannableDetailsDto {
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
    super(id, assetTag, barcode, name, weight, publicRentable, deleted, category, location, owner, createdAt, createdBy, updatedAt, updatedBy);
    this.manufacturer = manufacturer;
    this.model = model;
    this.serialNumber = serialNumber;
    this.estimatedValue = estimatedValue;
    this.status = status;
    this.quantity = quantity;
    this.acquisitionSource = acquisitionSource;
    this.acquisitionDate = acquisitionDate;
    this.warrantyEndDate = warrantyEndDate;
    this.notes = notes;
  }

  static override fromJson(json: Record<string, unknown>): DeviceDetails {
    return new DeviceDetails(
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
      json['manufacturer'] as string,
      json['model'] as string,
      json['serialNumber'] as string,
      json['estimatedValue'] as number,
      json['status'] as DeviceStatus,
      json['quantity'] as number,
      json['acquisitionSource'] as string,
      new Date(json['acquisitionDate'] as string),
      new Date(json['warrantyEndDate'] as string),
      json['notes'] as string
    );
  }
}