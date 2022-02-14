import {Scannable} from './Scannable';
import {Location} from './Location';
import {DeviceStatus} from './DeviceStatus';
import {Category} from './Category';

export class Device extends Scannable {
    maker: string;
    type: string;
    serial: string;
    value: number;
    weight: number;
    location: Location;
    status: DeviceStatus;
    category: Category;
    quantity: number;

    static toJsonString(device: Device): string {
        const deviceJson = JSON.parse(JSON.stringify(device));
        deviceJson['@type'] = 'device';
        return `{\"Device\": ${JSON.stringify(deviceJson)}}`;
    }

    static fromJson(deviceJson: Device): Device {
        return new Device(
            deviceJson.id,
            deviceJson.name,
            deviceJson.barcode,
            deviceJson.textIdentifier,
            deviceJson.maker,
            deviceJson.type,
            deviceJson.serial,
            deviceJson.value,
            deviceJson.weight,
            deviceJson.location,
            deviceJson.status,
            deviceJson.category,
            deviceJson.quantity
        )
    }

    constructor(id: number = -1, name: string = '', barcode: string = '', textIdentifier: string = '', maker: string = '',
                type: string = '', serial: string = '', value: number = 0, weight: number = 0, location: Location = null,
                status: DeviceStatus = DeviceStatus.GOOD, category: Category = null, quantity: number = 1) {
        super('device', id, name, barcode, textIdentifier);
        this.maker = maker;
        this.type = type;
        this.serial = serial;
        this.value = value;
        this.weight = weight;
        this.location = location;
        this.status = status;
        this.category = category;
        this.quantity = quantity;
    }

    getWeight() {
        return this.weight;
    }

    toJson(): String {
        const deviceJson = JSON.parse(JSON.stringify(this));
        deviceJson['@type'] = 'device';
        return `{\"Device\": ${JSON.stringify(deviceJson)}}`;
    }
}
