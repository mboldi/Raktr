import { DeviceStatus } from './deviceStatus';
import { OwnerDetailsDto } from '../../owner/ownerDetailsDto';

/**
 * Raw value of the device form in DeviceFormComponent. The form itself is an
 * UntypedFormGroup, so this describes the shape the DTOs read off it rather
 * than a type the form guarantees.
 */
export interface DeviceFormValue {
  assetTag: string;
  barcode: string;
  name: string;
  weight: number;
  isPublicRentable: boolean;
  category: string;
  location: string;
  owner: OwnerDetailsDto;
  manufacturer: string;
  model: string;
  serialNumber: string;
  estimatedValue: number;
  status: DeviceStatus;
  quantity: number;
  acquisitionSource: string;
  acquisitionDate: Date | null;
  warrantyEndDate: Date | null;
  notes: string;
}
