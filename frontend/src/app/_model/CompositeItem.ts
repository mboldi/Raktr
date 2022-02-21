import {Scannable} from './Scannable';
import {Device} from './Device';
import {Location} from './Location';
import {Category} from './Category';

export class CompositeItem extends Scannable {
    type_ = 'compositeItem';
    devices: Device[];

    static fromJson(compositeItem: CompositeItem): CompositeItem {
        const compositeItem1 = new CompositeItem(compositeItem.id,
            compositeItem.name,
            compositeItem.barcode,
            compositeItem.textIdentifier,
            compositeItem.isPublicRentable,
            [],
            compositeItem.category,
            compositeItem.location);

        if (compositeItem.devices) {
            compositeItem.devices.forEach(deviceJson => compositeItem1.devices.push(new Device(
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
            )));
        } else {
            compositeItem1.devices = [];
        }

        return compositeItem1
    }

    static toJsonString(compositeItem: CompositeItem): string {
        return `{\"CompositeItem\": ${this.toJsonWithoutRoot(compositeItem)}}`;
    }

    static toJsonWithoutRoot(compositeItem: CompositeItem): string {
        const compositeJson = JSON.parse(JSON.stringify(compositeItem));
        compositeJson['@type'] = 'compositeItem';
        compositeJson['devices'].forEach(device => device['@type'] = 'device');
        return JSON.stringify(compositeJson);
    }

    constructor(id: number = -1, name: string = '', barcode: string = '', textIdentifier: string = '', isPublicRentable: boolean = false,
                devices: Device[] = [], category: Category = null, location: Location = null) {
        super('compositeItem', id, name, barcode, textIdentifier, category, location, isPublicRentable);
        this.devices = devices;
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
