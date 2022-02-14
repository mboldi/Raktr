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
                CompositeItem.fromJSON(rentItemString.scannable as CompositeItem),
            this.rentStatusFormatter(rentItemString.backStatus),
            rentItemString.outQuantity)
    }

    static rentStatusFormatter(status: number | string): BackStatus {
        if (status === 0 || status === 'OUT') {
            return BackStatus.OUT;
        } else if (status === 1 || status === 'BACK') {
            return BackStatus.BACK;
        } else if (status === 2 || status === 'PACKED_IN') {
            return BackStatus.PACKED_IN;
        }
    }

    static toJson(rentItem: RentItem): string {
        const rentItemJson = JSON.parse(JSON.stringify(rentItem));

        rentItemJson['scannable'] = rentItem.scannable;
        rentItemJson['scannable']['@type'] = rentItem.scannable.type_;
        return `{\"RentItem\": ${JSON.stringify(rentItem)}}`;
    }

    constructor(id: number = -1, scannable: Scannable = null, backStatus: BackStatus = BackStatus.OUT, outQuantity: number = 1) {
        this.id = id;
        this.scannable = scannable;
        this.backStatus = backStatus;
        this.outQuantity = outQuantity;
    }
}
