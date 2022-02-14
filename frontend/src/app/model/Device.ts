import {Scannable} from './Scannable';
import {Location} from './Location';
import {DeviceStatus} from './DeviceStatus';
import {Category} from './Category';
import {Owner} from './Owner';

export class Device extends Scannable {
    maker: string;
    type: string;
    serial: string;
    value: number;
    weight: number;
    status: DeviceStatus;
    quantity: number;
    aquiredFrom: string;
    dateOfAcquisition: Date;
    owner: Owner;
    endOfWarranty: Date;
    comment: string;

    static toJsonString(device: Device): string {
        return `{\"Device\": ${this.toJsonWithoutRoot(device)}}`;
    }

    static toJsonWithoutRoot(device: Device): string {
        const deviceJson = JSON.parse(JSON.stringify(device));
        deviceJson['@type'] = 'device';
        return JSON.stringify(deviceJson);
    }

    static fromJson(deviceJson: Device): Device {
        return new Device(
            deviceJson.id,
            deviceJson.name,
            deviceJson.barcode,
            deviceJson.textIdentifier,
            deviceJson.isPublicRentable,
            deviceJson.maker,
            deviceJson.type,
            deviceJson.serial,
            deviceJson.value,
            deviceJson.weight,
            deviceJson.location,
            deviceJson.status,
            deviceJson.category,
            deviceJson.quantity,
            deviceJson.aquiredFrom,
            deviceJson.dateOfAcquisition,
            deviceJson.owner,
            deviceJson.endOfWarranty,
            deviceJson.comment
        )
    }

    constructor(id: number = -1, name: string = '', barcode: string = '', textIdentifier: string = '', isPublicRentable: boolean = false,
                maker: string = '', type: string = '', serial: string = '', value: number = 0, weight: number = 0,
                location: Location = null, status: DeviceStatus = DeviceStatus.GOOD, category: Category = null, quantity: number = 1,
                acquiredFrom: string = '', dateOfAcquisition: Date = new Date(), owner: Owner = null, endOfWarranty: Date = null,
                comment: string = '') {
        super('device', id, name, barcode, textIdentifier, category, location, isPublicRentable);
        this.maker = maker;
        this.type = type;
        this.serial = serial;
        this.value = value;
        this.weight = weight;
        this.status = status;
        this.quantity = quantity;
        this.aquiredFrom = acquiredFrom;
        this.dateOfAcquisition = dateOfAcquisition;
        this.owner = owner;
        this.endOfWarranty = endOfWarranty;
        this.comment = comment;
    }

    getWeight() {
        return this.weight;
    }

    toJson(): String {
        return Device.toJsonString(this);
    }
}
