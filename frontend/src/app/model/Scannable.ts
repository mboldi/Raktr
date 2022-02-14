import {Category} from './Category';
import {Location} from './Location';

export abstract class Scannable {
    type_: string;
    id: number;
    name: string;
    barcode: string;
    textIdentifier: string;
    category: Category;
    location: Location;
    isPublicRentable: boolean;

    constructor(type_: string, id: number, name: string, barcode: string, textIdentifier: string,
                category: Category, location: Location, isPublicRentable: boolean) {
        this.type_ = type_;
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.textIdentifier = textIdentifier;
        this.category = category;
        this.location = location;
        this.isPublicRentable = isPublicRentable;
    }

    abstract getWeight(): number;

    abstract toJson(): String;
}

