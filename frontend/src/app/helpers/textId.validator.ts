import {FormControl} from '@angular/forms';
import {ScannableService} from '../_services/scannable.service';
import {map} from 'rxjs/operators';

export const textIdValidator =
    (scannableService: ScannableService, currentId: number) => {
        return (control: FormControl) => {
            return scannableService.getTakenByTextId(control.value).pipe(
                map(res => {
                    return res === currentId || res === -1 ? null : {'textIdTaken': true};
                })
            );
        };
    };
