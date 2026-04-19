import {Component, OnInit} from '@angular/core';
import {
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatTable
} from "@angular/material/table";
import {MatFormField, MatInput, MatLabel, MatSuffix} from "@angular/material/input";
import {DeviceService} from "../../../../services/device.service";
import {DeviceDetails} from "../../../../model/scannable/device/DeviceDetails";
import {MatPaginator} from "@angular/material/paginator";
import {OwnerDetailsDto} from "../../../../model/owner/ownerDetailsDto";
import {UserDetails} from "../../../../model/user/userDetails";
import {DeviceStatus} from "../../../../model/scannable/device/deviceStatus";
import {DecimalPipe} from "@angular/common";
import {MatSortModule, Sort} from "@angular/material/sort";
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";

const ALL_COLUMNS: string[] = ['name', 'assetTag', 'maker', 'model', 'quantity', 'category', 'location', 'weight'];
const REDUCED_COLUMNS: string[] = ['name', 'assetTag', 'maker', 'model', 'quantity'];

@Component({
    selector: 'app-devices',
    imports: [
        MatTable,
        MatFormField,
        MatLabel,
        MatInput,
        MatColumnDef,
        MatHeaderCell,
        MatCell,
        MatCellDef,
        MatHeaderCellDef,
        MatPaginator,
        MatHeaderRow,
        MatRow,
        MatHeaderRowDef,
        MatRowDef,
        DecimalPipe,
        MatSortModule,
        FormsModule,
        MatIcon,
        MatIconButton,
        MatSuffix,
        ReactiveFormsModule
    ],
    templateUrl: './devices.component.html',
    styleUrl: './devices.component.scss',
})
export class DevicesComponent implements OnInit {

    protected deviceSearchFormControl = new FormControl();

    protected displayedColumns = ALL_COLUMNS;

    protected devices: DeviceDetails[] = [
        new DeviceDetails(1, "B-CAM-01", "0000001", "320-1", 8654, false, false, "Videó", "Stúdiótér", new OwnerDetailsDto(1, "SVIE", true, new Date(), new UserDetails("1qda", "mboldi", "Márta", "Boldizsár", "ifjB", "", []), new Date(), new UserDetails("1qda", "mboldi", "Márta", "Boldizsár", "ifjB", "", [])), new Date(), new UserDetails("1qda", "mboldi", "Márta", "Boldizsár", "ifjB", "", []), new Date(), new UserDetails("1qda", "mboldi", "Márta", "Boldizsár", "ifjB", "", []), "Sony", "PMW-320", "000000111", 85000, DeviceStatus.GOOD, 1, "KPR", new Date(), new Date(), ""),
        new DeviceDetails(1, "B-CAM-02", "0000002", "500-1", 8654, false, false, "Videó", "Stúdiótér", new OwnerDetailsDto(1, "SVIE", true, new Date(), new UserDetails("1qda", "mboldi", "Márta", "Boldizsár", "ifjB", "", []), new Date(), new UserDetails("1qda", "mboldi", "Márta", "Boldizsár", "ifjB", "", [])), new Date(), new UserDetails("1qda", "mboldi", "Márta", "Boldizsár", "ifjB", "", []), new Date(), new UserDetails("1qda", "mboldi", "Márta", "Boldizsár", "ifjB", "", []), "Sony", "PMW-320", "000000111", 85000, DeviceStatus.GOOD, 1, "KPR", new Date(), new Date(), "")
    ];

    constructor(
        private deviceService: DeviceService,) {
    }

    ngOnInit() {
        /*this.deviceService.getDevices().subscribe(devices => {
          this.devices = devices;
        });*/
    }

    protected applyFilter($event: KeyboardEvent) {
    }

    protected announceSortChange($event: Sort) {
        console.log($event);
    }
}
