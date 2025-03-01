import {DeviceStatus} from './DeviceStatus';
import {Owner} from './Owner';
import {Category} from './Category';
import {Location} from './Location';
import {Device} from "./Device";

export class DeviceForExcel {
    id: number;
    name: string;
    barcode: string;
    textIdentifier: string;
    category: string;
    location: string;
    isPublicRentable: boolean;

    maker: string;
    type: string;
    serial: string;
    value: number;
    weight: number;
    status: string;
    quantity: number;
    aquiredFrom: string;
    dateOfAcquisition: string;
    owner: string;
    endOfWarranty: string;
    comment: string;


    constructor(device: Device) {
        this.id = device.id;
        this.name = device.name;
        this.barcode = device.barcode;
        this.textIdentifier = device.textIdentifier;
        this.category = device.category.name;
        this.location = device.location.name;
        this.isPublicRentable = device.isPublicRentable;

        this.maker = device.maker;
        this.type = device.type;
        this.serial = device.serial;
        this.value = device.value;
        this.weight = device.weight;
        this.status = device.status.toString();
        this.quantity = device.quantity;
        this.aquiredFrom = device.aquiredFrom;
        this.dateOfAcquisition = device.dateOfAcquisition ? device.dateOfAcquisition.toLocaleDateString('hu-HU') : '';
        this.owner = device.owner ? device.owner.name : '';
        this.endOfWarranty = device.endOfWarranty ? device.endOfWarranty.toLocaleDateString('hu-HU') : '';
        this.comment = device.comment;
    }
}
