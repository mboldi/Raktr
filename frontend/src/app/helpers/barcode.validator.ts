import {FormControl} from '@angular/forms';
import {ScannableService} from '../_services/scannable.service';
import {map} from 'rxjs/operators';

export const barcodeValidator =
    (scannableService: ScannableService, currentId: number) => {
        return (control: FormControl) => {
            return scannableService.getTakenByBarcode(control.value).pipe(
                map(res => {
                    return res === currentId || res === -1 ? null : {'barcodeTaken': true};
                })
            );
        };
    };
