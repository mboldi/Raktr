import {Scannable} from './Scannable';
import {BackStatus} from './BackStatus';
import {Device} from './Device';
import {CompositeItem} from './CompositeItem';

export class RentItem {
    id: number;
    scannable: Scannable;
    backStatus: BackStatus;
    outQuantity: number;

    static fromJson(rentItemString: RentItem): RentItem {
        return new RentItem(rentItemString.id,
            rentItemString.scannable['@type'] === 'device' ?
                Device.fromJson(rentItemString.scannable as Device) :
                CompositeItem.fromJson(rentItemString.scannable as CompositeItem),
            this.rentStatusFormatter(rentItemString.backStatus),
            rentItemString.outQuantity)
    }

    static rentStatusFormatter(status: number | string): BackStatus {
        if (status === 0 || status === 'OUT') {
            return BackStatus.OUT;
        } else if (status === 1 || status === 'BACK') {
            return BackStatus.BACK;
        } else if (status === 2 || status === 'PLANNED') {
            return BackStatus.PLANNED;
        }
    }

    static toJson(rentItem: RentItem): string {
        const rentItemJson = JSON.parse(JSON.stringify(rentItem));

        rentItemJson['scannable'] = rentItem.scannable.type_ === 'device' ?
            JSON.parse(Device.toJsonWithoutRoot(rentItem.scannable as Device)) :
            JSON.parse(CompositeItem.toJsonWithoutRoot(rentItem.scannable as CompositeItem));
        return `{\"RentItem\": ${JSON.stringify(rentItemJson)}}`;
    }

    constructor(id: number = -1, scannable: Scannable = null, backStatus: BackStatus = BackStatus.OUT, outQuantity: number = 1) {
        this.id = id;
        this.scannable = scannable;
        this.backStatus = backStatus;
        this.outQuantity = outQuantity;
    }
}
