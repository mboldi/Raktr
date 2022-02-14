import {Scannable} from './Scannable';
import {Device} from './Device';
import {Location} from './Location';

export class CompositeItem extends Scannable {
    type_ = 'compositeItem';
    devices: Device[];
    location: Location;

    static fromJSON(compositeItem: CompositeItem): CompositeItem {
        const compositeItem1 = new CompositeItem(compositeItem.id,
            compositeItem.name,
            compositeItem.barcode,
            compositeItem.textIdentifier,
            [],
            compositeItem.location);

        compositeItem.devices.forEach(deviceJson => compositeItem1.devices.push(new Device(
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
        )));

        return compositeItem1
    }

    static toJsonString(compositeItem: CompositeItem): string {
        const compositeJson = JSON.parse(JSON.stringify(compositeItem));
        compositeJson['@type'] = 'compositeItem';
        return `{\"CompositeItem\": ${JSON.stringify(compositeJson)}}`;
    }

    constructor(id: number = -1, name: string = '', barcode: string = '', textIdentifier: string = '',
                devices: Device[] = [], location: Location = null) {
        super('compositeItem', id, name, barcode, textIdentifier);
        this.devices = devices;
        this.location = location;
    }

    getWeight() {
        let sum = 0;

        this.devices.forEach(device => {
            sum += device.weight;
        });

        return sum;
    }

    toJson(): String {
        const compositeJson = JSON.parse(JSON.stringify(this));
        compositeJson['@type'] = 'compositeItem';
        return `{\"CompositeItem\": ${JSON.stringify(compositeJson)}}`;
    }
}
