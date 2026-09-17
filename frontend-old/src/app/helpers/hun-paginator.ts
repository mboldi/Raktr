import {Subject} from 'rxjs';
import {MatPaginatorIntl} from '@angular/material/paginator';
import {Injectable} from '@angular/core';

@Injectable()
export class HunPaginator implements MatPaginatorIntl {
    changes = new Subject<void>();

    // For internationalization, the `$localize` function from
    // the `@angular/localize` package can be used.
    firstPageLabel = $localize`Első oldal`;
    itemsPerPageLabel = $localize`Elemek száma oldalanként:`;
    lastPageLabel = $localize`Utolsó oldal`;

    // You can set labels to an arbitrary string too, or dynamically compute
    // it through other third-party internationalization libraries.
    nextPageLabel = 'Következő oldal';
    previousPageLabel = 'Előző oldal';

    getRangeLabel(page: number, pageSize: number, length: number): string {
        if (length === 0) {
            return $localize`Oldal száma: 1 ennyiből: 1`;
        }
        const amountPages = Math.ceil(length / pageSize);
        return $localize`Oldal száma: ${page + 1} ennyiből: ${amountPages}`;
    }
}
