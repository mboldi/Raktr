import {Component, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import 'bootstrap-notify'
import {Rent} from '../model/Rent';
import {RentService} from '../_services/rent.service';
import {FormControl} from '@angular/forms';
import {map, startWith} from 'rxjs/operators';
import {Router} from '@angular/router';
import {MatPaginatorIntl, PageEvent} from '@angular/material/paginator';
import {HunPaginator} from '../helpers/hun-paginator';

@Component({
    selector: 'app-rents',
    templateUrl: './rents.component.html',
    styleUrls: ['./rents.component.css'],
    providers: [Title,
        {provide: MatPaginatorIntl, useClass: HunPaginator}]
})
export class RentsComponent implements OnInit {
    rents: Rent[] = [];
    filteredRents: Rent[];
    pagedRents: Rent[];

    currPageIndex = 0;
    currPageSize = 25;

    rentSearchControl = new FormControl();

    constructor(private title: Title,
                private rentService: RentService,
                private router: Router) {
        this.title.setTitle('Raktr - Kivitelek');
    }

    ngOnInit(): void {
        this.rentSearchControl.setValue('');

        this.rentService.getRents().subscribe(rents => {
            this.rents = rents;

            this.rents = this.rents.sort(((a, b) => {
                const aDate = new Date(a.backDate === null ? a.backDate : a.outDate);
                const bDate = new Date(b.backDate === null ? b.backDate : b.outDate);

                return aDate.getTime() - bDate.getTime();
            }));
            this.rentSearchControl.setValue('');

            this.setPage();
        });

        this.rentSearchControl.valueChanges.subscribe(value => {
            this.filteredRents = this._filterDevices(this.rents, value);

            this.setPage();
        });
    }

    private _filterDevices(rents_: Rent[], value: string): Rent[] {
        const filterValue = value.toLowerCase();

        return rents_.filter(rent => rent.destination.toLowerCase().includes(filterValue) ||
            rent.issuer.nickName.toLowerCase().includes(filterValue) ||
            rent.renter.toLowerCase().includes(filterValue));
    }

    private setPage() {
        for (; this.filteredRents.length < this.currPageIndex * this.currPageSize; this.currPageIndex--) {
        }

        this.pagedRents = this.filteredRents.slice(this.currPageIndex * this.currPageSize,
            (this.currPageIndex + 1) * this.currPageSize);
    }

    pageChanged(event: PageEvent) {
        this.currPageIndex = event.pageIndex;
        this.currPageSize = event.pageSize;

        this.setPage();
    }

    openRent(id: number) {
        this.router.navigateByUrl('/rent/' + id);
    }
}
