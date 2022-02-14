import {Device} from './Device';
import {CompositeItem} from './CompositeItem';

export abstract class Scannable {
    type_: string;
    id: number;
    name: string;
    barcode: string;
    textIdentifier: string;

    constructor(type_: string, id: number, name: string, barcode: string, textIdentifier: string) {
        this.type_ = type_;
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.textIdentifier = textIdentifier;
    }

    abstract getWeight(): number;

    abstract toJson(): String;
}

