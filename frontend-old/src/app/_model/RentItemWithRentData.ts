import {RentItem} from './RentItem';
import {Rent} from './Rent';

export class RentItemWithRentData {
    rentItem: RentItem;
    rent: Rent;

    static fromJson(rentItemWithRentData: RentItemWithRentData): RentItemWithRentData {
        return new RentItemWithRentData(
            RentItem.fromJson(rentItemWithRentData.rentItem),
            Rent.fromJson(rentItemWithRentData.rent)
        );
    }

    constructor(rentItem: RentItem, rent: Rent) {
        this.rentItem = rentItem;
        this.rent = rent;
    }
}
