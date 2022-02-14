import {Component, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {FormControl} from '@angular/forms';
import {Device} from '../model/Device';
import {DeviceService} from '../_services/device.service';
import {CompositeItem} from '../model/CompositeItem';
import {CompositeService} from '../_services/composite.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {EditDeviceModalComponent} from '../edit-device-modal/edit-device-modal.component';
import {EditCompositeModalComponent} from '../edit-composite-modal/edit-composite-modal.component';
import * as $ from 'jquery';
import {Sort} from '@angular/material/sort';
import {MatPaginatorIntl, PageEvent} from '@angular/material/paginator';
import {Category} from '../model/Category';
import {DeviceStatus} from '../model/DeviceStatus';
import {Location} from '../model/Location';
import {HunPaginator} from '../helpers/hun-paginator';

@Component({
    selector: 'app-table-list',
    templateUrl: './devices.component.html',
    styleUrls: ['./devices.component.css'],
    providers: [Title,
        {provide: MatPaginatorIntl, useClass: HunPaginator}]
})
export class DevicesComponent implements OnInit {
    currentTab = 'devices';

    searchControl = new FormControl();

    devices: Device[];
    sortedDevices: Device[];
    pagedDevices: Device[];

    currDevicePageIndex = 0;
    currDevicePageSize = 25;

    compositeItems: CompositeItem[];
    sortedComposites: CompositeItem[];
    pagedComposites: CompositeItem[];

    currCompositePageIndex = 0;
    currCompositePageSize = 25;

    constructor(private title: Title,
                private deviceService: DeviceService,
                private compositeService: CompositeService,
                private modalService: NgbModal) {
        this.title.setTitle('Raktr - Eszközök');
    }

    ngOnInit() {
        this.searchControl.setValue('');

        this.devices = [];

        this.deviceService.getDevices().subscribe(devices => {
            this.devices = devices;
            this.sortedDevices = devices;
            this.setDevicePage();

            this.searchControl.valueChanges.subscribe(value => {
                this.sortedDevices = this.devices.filter(device =>
                    device.name.toLowerCase().includes(value.toLowerCase()) ||
                    device.maker.toLowerCase().includes(value.toLowerCase()) ||
                    device.type.toLowerCase().includes(value.toLowerCase()) ||
                    device.location.name.toLowerCase().includes(value.toLowerCase()) ||
                    device.category.name.toLowerCase().includes(value.toLowerCase()) ||
                    device.textIdentifier.toLowerCase().includes(value.toLowerCase()) ||
                    device.barcode.toLowerCase().includes(value.toLowerCase()));

                this.setDevicePage();
            });
        });

        this.getComposites();

        this.searchControl.valueChanges.subscribe(value => {
            this.sortedComposites = this.compositeItems.filter(compositeItem =>
                compositeItem.name.toLowerCase().includes(value) ||
                compositeItem.textIdentifier.toLowerCase().includes(value) ||
                compositeItem.barcode.toLowerCase().includes(value))
        });
    }

    private setDevicePage() {
        for (; this.sortedDevices.length < this.currDevicePageIndex * this.currDevicePageSize; this.currDevicePageIndex--) {
        }

        this.pagedDevices = this.sortedDevices.slice(this.currDevicePageIndex * this.currDevicePageSize,
            (this.currDevicePageIndex + 1) * this.currDevicePageSize);
    }

    devicePageChanged(event: PageEvent) {
        this.currDevicePageIndex = event.pageIndex;
        this.currDevicePageSize = event.pageSize;

        this.setDevicePage();
    }

    private setCompositePage() {
        for (; this.sortedComposites.length < this.currCompositePageIndex * this.currCompositePageSize; this.currCompositePageIndex--) {
        }

        this.pagedComposites = this.sortedComposites.slice(this.currCompositePageIndex * this.currCompositePageSize,
            (this.currCompositePageIndex + 1) * this.currCompositePageSize);
    }

    compositePageChanged(event: PageEvent) {
        this.currCompositePageIndex = event.pageIndex;
        this.currCompositePageSize = event.pageSize;

        this.setCompositePage();
    }

    getComposites() {
        this.compositeItems = [];

        this.compositeService.getCompositeItems().subscribe(compositeItems => {
            this.compositeItems = compositeItems;
            this.sortedComposites = compositeItems;

            this.setCompositePage();
        });
    }

    sortDevices(sort: Sort) {
        if (this.devices.length === 0) {
            return;
        }
        const data = this.devices.slice();
        if (!sort.active || sort.direction === '') {
            this.sortedDevices = data;
            return;
        }

        this.sortedDevices = data.sort((a, b) => {
            const isAsc = sort.direction === 'asc';
            switch (sort.active) {
                case 'name':
                    return compare(a.name.toLowerCase(), b.name.toLowerCase(), isAsc);
                case 'maker':
                    return compare(a.maker.toLowerCase(), b.maker.toLowerCase(), isAsc);
                case 'type':
                    return compare(a.type.toLowerCase(), b.type.toLowerCase(), isAsc);
                case 'quantity':
                    return compare(a.quantity, b.quantity, isAsc);
                case 'category':
                    return compare(a.category.name.toLowerCase(), b.category.name.toLowerCase(), isAsc);
                case 'location':
                    return compare(a.location.name.toLowerCase(), b.location.name.toLowerCase(), isAsc);
                case 'weight':
                    return compare(a.weight, b.weight, isAsc);
                case 'textId':
                    return compare(a.textIdentifier.toLowerCase(), b.textIdentifier.toLowerCase(), isAsc);
                default:
                    return 0;
            }
        });

        this.setDevicePage();
    }

    sortComposites(sort: Sort) {
        if (this.compositeItems.length === 0) {
            return;
        }
        const data = this.compositeItems.slice();
        if (!sort.active || sort.direction === '') {
            this.sortedComposites = data;
            return;
        }

        this.sortedComposites = data.sort((a, b) => {
            const isAsc = sort.direction === 'asc';
            switch (sort.active) {
                case 'name':
                    return compare(a.name.toLowerCase(), b.name.toLowerCase(), isAsc);
                case 'category':
                    return compare(a.category.name.toLowerCase(), b.category.name.toLowerCase(), isAsc);
                case 'location':
                    return compare(a.location.name.toLowerCase(), b.location.name.toLowerCase(), isAsc);
                case 'weight':
                    return compare(a.getWeight(), b.getWeight(), isAsc);
                case 'textId':
                    return compare(a.textIdentifier.toLowerCase(), b.textIdentifier.toLowerCase(), isAsc);
                default:
                    return 0;
            }
        });

        this.setCompositePage();
    }

    copyDevice(device: Device) {
        const editModal = this.modalService.open(EditDeviceModalComponent, {size: 'lg', windowClass: 'modal-holder'});
        editModal.componentInstance.title = 'Új eszköz másik alapján';
        editModal.componentInstance.device = device;
        editModal.componentInstance.device.id = -1;

        editModal.result.catch(reason => {
            this.deviceService.getDevices().subscribe(devices => {
                this.devices = devices;
                this.sortedDevices = devices;

                this.setDevicePage();
            });
        })
    }

    editDevice(device: Device) {
        const editModal = this.modalService.open(EditDeviceModalComponent, {size: 'lg', windowClass: 'modal-holder'});
        editModal.componentInstance.title = 'Eszköz szerkesztése';
        editModal.componentInstance.device = device;

        editModal.result.catch(reason => {
            if (reason === 'delete') {
                this.deviceService.getDevices().subscribe(devices => {
                    this.devices = devices;
                    this.sortedDevices = devices;

                    this.setDevicePage();
                });
            }
        })
    }

    editCompositeItem(compositeItem: CompositeItem) {
        const editModal = this.modalService.open(EditCompositeModalComponent, {size: 'lg', windowClass: 'modal-holder'});
        editModal.componentInstance.title = 'Összetett eszköz szerkesztése';
        editModal.componentInstance.compositeItem = compositeItem;

        editModal.result.catch(reason => {
            this.getComposites();
        });
    }

    create() {
        switch (this.currentTab) {
            case 'devices':
                const editDeviceModal = this.modalService.open(EditDeviceModalComponent, {size: 'lg', windowClass: 'modal-holder'});
                editDeviceModal.componentInstance.title = 'Új eszköz';

                editDeviceModal.result.catch(result => {
                    if (result !== 0) {
                        const index = this.devices.indexOf(result);
                        if (index === -1) {
                            this.devices.push(result as Device);
                            this.searchControl.setValue('');
                            this.showNotification(result.name + ' hozzáadva sikeresen!', 'success');
                        } else {
                            this.devices[index] = (result as Device);
                        }

                        this.setDevicePage();
                    }
                });
                break;
            case 'composites':
                const editCompositeModal = this.modalService.open(EditCompositeModalComponent, {size: 'lg', windowClass: 'modal-holder'});
                editCompositeModal.componentInstance.title = 'Új összetett eszköz';
                editCompositeModal.result.catch(reason => {
                    this.getComposites();
                });
                break;
        }
    }

    setTab(tabName: string) {
        this.currentTab = tabName;
        this.searchControl.setValue('');
    }

    showNotification(message_: string, type: string) {
        $['notify']({
            icon: 'add_alert',
            message: message_
        }, {
            type: type,
            timer: 1000,
            placement: {
                from: 'top',
                align: 'right'
            },
            z_index: 2000
        })
    }

    addLotOfDevices(start: number, num: number) {
        for (let i = start; i < start + num; i++) {
            this.deviceService.addDevice(new Device(
                -1,
                `name:_${i}`,
                (i).toString().padStart(7, '0'),
                `azonos_${i}`,
                false,
                `gyartooo_${i}`,
                `tippuuuuus_${i}`,
                `kredenc_${i}`,
                1000 * (i % 5) + 2 * i,
                100 * (i % 5) + 2 * i,
                new Location(-1, 'almaaa'),
                DeviceStatus.GOOD,
                new Category(-1, 'asd'),
            )).subscribe(device => {
                console.log(`${device.name} added`);
            })
        }
    }
}

function compare(a: number | string, b: number | string, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}
