import {Component, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import 'bootstrap-notify'
import {Rent} from '../model/Rent';
import {RentService} from '../services/rent.service';
import {FormControl} from '@angular/forms';
import {map, startWith} from 'rxjs/operators';
import {Device} from '../model/Device';
import {Observable} from 'rxjs';

@Component({
    selector: 'app-rents',
    templateUrl: './rents.component.html',
    styleUrls: ['./rents.component.css'],
    providers: [Title]
})
export class RentsComponent implements OnInit {
    rents: Rent[] = [];
    filteredRents: Observable<Rent[]>;

    currRent: Rent;
    rentSearchControl = new FormControl();

    constructor(private title: Title, private rentService: RentService) {
        this.title.setTitle('Raktr - Bérlések');
    }

    ngOnInit(): void {
        this.rentSearchControl.setValue('');

        this.rentService.getRents().subscribe(rents => this.rents = rents);

        this.filteredRents = this.rentSearchControl.valueChanges
            .pipe(
                startWith(''),
                map(value => this._filterDevices(this.rents, value))
            );
    }

    private _filterDevices(rents_: Rent[], value: string): Rent[] {
        const filterValue = value.toLowerCase();

        return rents_.filter(rent => rent.destination.toLowerCase().includes(filterValue) ||
            rent.issuer.toLowerCase().includes(filterValue) ||
            rent.renter.toLowerCase().includes(filterValue));
    }

    onSelect(rent: Rent) {
        this.currRent = rent;
    }
}
