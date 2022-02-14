import {RentItem} from './RentItem';
import {RentType} from './RentType';
import {Comment} from './Comment';
import {User} from './User';
import {Project} from './Project';

export class Rent {
    id: number;
    type: RentType;
    destination: string;
    renter: string;
    issuer: User;
    outDate: Date;
    expBackDate: Date;
    backDate: Date;
    isClosed: boolean;
    rentItems: RentItem[];
    comments: Comment[];
    project: Project;

    static toJsonWithoutRoot(rent: Rent): string {
        const rentJson = JSON.parse(JSON.stringify(rent));

        if (rentJson.rentItems !== undefined) {
            rentJson.rentItems.forEach(rentItem => {
                rentItem['scannable']['@type'] = rentItem['scannable']['type_']
                if (rentItem['scannable']['type_'] === 'compositeItem') {
                    rentItem['scannable']['devices'].forEach(device => {
                        device['@type'] = device['type_'];
                    })
                }
            })

            if (rentJson.comments !== undefined && rentJson.comments.length !== 0) {
                rentJson.comments.forEach(comment => {
                    comment['@type'] = comment['type_'];
                })
            }
        }

        return rentJson;
    }

    static toJsonString(rent: Rent): string {
        return `{\"Rent\": ${JSON.stringify(this.toJsonWithoutRoot(rent))}}`;
    }

    static fromJson(rent: Rent): Rent {
        const newRent = new Rent();
        newRent.id = rent.id;
        newRent.destination = rent.destination;
        newRent.issuer = rent.issuer;
        newRent.renter = rent.renter;
        newRent.outDate = rent.outDate;
        newRent.expBackDate = rent.expBackDate;
        newRent.backDate = rent.backDate;
        newRent.isClosed = rent.isClosed;
        newRent.rentItems = [];

        switch (rent.type.toString()) {
            case 'SIMPLE':
                newRent.type = RentType.SIMPLE;
                break;
            case 'COMPLEX':
                newRent.type = RentType.COMPLEX;
                break;
        }

        rent.rentItems.forEach(rentItem => newRent.rentItems.push(RentItem.fromJson(rentItem)));

        if (rent.comments) {
            if (!newRent.comments) {
                newRent.comments = [];
            }

            rent.comments.forEach(comment => newRent.comments.push(Comment.fromJson(comment)));
        } else {
            newRent.comments = [];
        }

        return newRent;
    }

    constructor(id: number = -1, rentType: RentType = RentType.SIMPLE, destination: string = '', renter: string = '', issuer: User = null,
                outDate: Date = null, expBackDate: Date = null, actBackDate: Date = null, isClosed: boolean = false,
                rentItems: RentItem[] = []) {
        this.id = id;
        this.type = rentType;
        this.destination = destination;
        this.renter = renter;
        this.issuer = issuer;
        this.outDate = outDate;
        this.backDate = actBackDate;
        this.isClosed = isClosed;
        this.rentItems = rentItems;
    }

    getSumWeight(): number {
        let sumWeight = 0;
        this.rentItems.forEach(rentItem => sumWeight += rentItem.outQuantity * rentItem.scannable.getWeight());

        return sumWeight;
    }
}
