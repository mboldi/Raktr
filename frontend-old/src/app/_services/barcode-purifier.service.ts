import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class BarcodePurifier {

    static purify(barcode: string): string {
        if (this.isEAN8(barcode)) {
            return barcode.substring(0, 7);
        } else {
            return barcode;
        }
    }

    static isEAN8(barcode: string): boolean {
        const checkSumInBarcode: number = +barcode[7];

        return barcode.length === 8
            && /^\d+$/.test(barcode)
            && checkSumInBarcode === this.calculateEAN8Checksum(barcode);
    }

    static calculateEAN8Checksum(barcode: string): number {
        let sum = 0;

        const times = [3, 1, 3, 1, 3, 1, 3];

        for (let i = 0; i < 7; i++) {
            sum += (+barcode[i]) * times[i];
        }

        return 10 - +(sum.toString().split('').pop());
    }
}
