import {Component, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {FormBuilder, FormGroup, UntypedFormControl} from '@angular/forms';
import {Device} from '../_model/Device';
import {DeviceService} from '../_services/device.service';
import {CompositeItem} from '../_model/CompositeItem';
import {CompositeService} from '../_services/composite.service';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {EditDeviceModalComponent} from '../edit-device-modal/edit-device-modal.component';
import {EditCompositeModalComponent} from '../edit-composite-modal/edit-composite-modal.component';
import * as $ from 'jquery';
import {Sort} from '@angular/material/sort';
import {MatPaginatorIntl, PageEvent} from '@angular/material/paginator';
import {Category} from '../_model/Category';
import {DeviceStatus} from '../_model/DeviceStatus';
import {Location} from '../_model/Location';
import {HunPaginator} from '../helpers/hun-paginator';
import {utils, writeFile} from 'xlsx';
import {User} from '../_model/User';
import {UserService} from '../_services/user.service';
import {DeviceForExcel} from '../_model/DeviceForExcel';
import {ActivatedRoute, Router} from '@angular/router';
import {Location as RouterLocation} from '@angular/common' ;
import {LocationService} from '../_services/location.service';
import {CategoryService} from '../_services/category.service';
import {DeviceImportModalComponent} from '../device-import-modal/device-import-modal.component';

@Component({
    selector: 'app-table-list',
    templateUrl: './devices.component.html',
    styleUrls: ['./devices.component.css'],
    providers: [Title,
        {provide: MatPaginatorIntl, useClass: HunPaginator}]
})
export class DevicesComponent implements OnInit {
    currentTab = 'devices';
    currUser: User = new User();

    searchControl = new UntypedFormControl();

    devices: Device[];
    sortedDevices: Device[] = [];
    pagedDevices: Device[];

    currDevicePageIndex = 0;
    currDevicePageSize = 25;

    compositeItems: CompositeItem[];
    sortedComposites: CompositeItem[] = [];
    pagedComposites: CompositeItem[];

    currCompositePageIndex = 0;
    currCompositePageSize = 25;

    locations: Location[] = [];
    categories: Category[] = [];
    locationGroup: FormGroup = this.formBuilder.group({});
    categoryGroup: FormGroup = this.formBuilder.group({});

    constructor(private title: Title,
                private deviceService: DeviceService,
                private compositeService: CompositeService,
                private modalService: NgbModal,
                private userService: UserService,
                private locationService: LocationService,
                private categoryService: CategoryService,
                private router: Router,
                private route: ActivatedRoute,
                private routerLocation: RouterLocation,
                private formBuilder: FormBuilder) {
        this.title.setTitle('Raktr - Eszközök');

        if (this.router.url.toString().includes('compositeItems')) {
            this.setTab('composites');
        }
    }

    ngOnInit() {

        this.searchControl.setValue('');

        this.userService.getCurrentUser().subscribe(user => {
            this.currUser = user;
        });

        this.devices = [];

        this.deviceService.getDevices().subscribe(devices => {
            this.devices = devices;
            this.sortedDevices = devices;
            this.setDevicePage();

            this.searchControl.valueChanges.subscribe(value => {
                this.filterDevices();
            });
        });

        this.getComposites();
        this.getLocAndCat();

        this.searchControl.valueChanges.subscribe(value => {
            this.filterCompositeItems();
        });

        // opening device if ID in URL is present

        if (this.route.snapshot.paramMap.get('id') !== null) {
            const id = this.route.snapshot.paramMap.get('id') as unknown as number;

            if (this.currentTab === 'devices') {
                this.deviceService.getDevice(id).subscribe(dev => {
                    this.editDevice(dev);
                }, error => {
                    this.showNotification('Nem találtam eszközt az URL-ben megadott ID-vel!', 'danger');
                    this.router.navigateByUrl('/devices');
                });
            } else {
                this.compositeService.getCompositeItem(id).subscribe(compositeItem => {
                    this.editCompositeItem(compositeItem);
                }, error => {
                    this.showNotification('Nem találtam eszközt az URL-ben megadott ID-vel!', 'danger');
                    this.router.navigateByUrl('/compositeItems');
                });
            }
        }
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
        if (this.sortedDevices.length === 0) {
            return;
        }
        const data = this.sortedDevices.slice();
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
        if (this.sortedComposites.length === 0) {
            return;
        }
        const data = this.sortedComposites.slice();
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

    private getDevices() {
        this.deviceService.getDevices().subscribe(devices => {
            this.devices = devices;
            this.sortedDevices = devices;

            this.setDevicePage();
        });
    }


    editDevice(device: Device) {
        const editModal = this.modalService.open(EditDeviceModalComponent, {size: 'lg'});
        editModal.componentInstance.title = 'Eszköz szerkesztése';
        editModal.componentInstance.device = device;

        this.routerLocation.go(`/devices/${device.id}`);

        editModal.result.catch(reason => {
            if (reason === 'copy') {
                const copyModal = this.modalService.open(EditDeviceModalComponent, {size: 'lg'});
                copyModal.componentInstance.title = 'Új eszköz másik alapján';
                copyModal.componentInstance.device = structuredClone(device);
                copyModal.componentInstance.device.id = -1;

                copyModal.result.catch(result => {
                    if (result.type_ !== undefined) {
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
                })
            }

            if (reason === 'delete' || reason === 'add') {
                this.deviceService.getDevices().subscribe(devices => {
                    this.devices = devices;
                    this.sortedDevices = devices;

                    this.setDevicePage();
                });
            }

            if (reason !== 'noRedirect') {
                this.routerLocation.go('/devices');
            }

            this.getLocAndCat();
        });
    }

    editCompositeItem(compositeItem: CompositeItem) {
        const editModal = this.modalService.open(EditCompositeModalComponent, {size: 'lg'});
        editModal.componentInstance.title = 'Összetett eszköz szerkesztése';
        editModal.componentInstance.compositeItem = compositeItem;

        this.routerLocation.go(`/compositeItems/${compositeItem.id}`);

        editModal.result.catch(reason => {
            this.getComposites();

            if (reason !== 'noRedirect') {
                this.routerLocation.go('/compositeItems');
            }

            this.getLocAndCat();
        });
    }

    create() {
        switch (this.currentTab) {
            case 'devices':
                const editDeviceModal = this.modalService.open(EditDeviceModalComponent, {size: 'lg'});
                editDeviceModal.componentInstance.title = 'Új eszköz';

                editDeviceModal.result.catch(result => {
                    if (result !== 0 && result !== 1) {
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
                const editCompositeModal = this.modalService.open(EditCompositeModalComponent, {size: 'lg'});
                editCompositeModal.componentInstance.title = 'Új összetett eszköz';
                editCompositeModal.result.catch(() => {
                    this.getComposites();
                });
                break;
        }
    }

    setTab(tabName: string) {
        this.currentTab = tabName;
        this.searchControl.setValue('');
    }

    handleExcelImport($event: any) {
        const files = $event.target.files;
        if (files.length) {
            const file = files[0];

            const importModal = this.modalService.open(DeviceImportModalComponent, {size: 'lg'});
            importModal.componentInstance.importFile = file;

            importModal.result.catch(reason => {
                this.showNotification('Eszközimport befejeződött', 'success');

                this.getDevices();

                $event.target.value = null;
            });
        }
    }

    exportDevices() {
        const header = [[
            'id',
            'name',
            'barcode',
            'textIdentifier',
            'category',
            'location',
            'isPublicRentable',
            'maker',
            'type',
            'serial',
            'value',
            'weight',
            'status',
            'quantity',
            'acquiredFrom',
            'dateOfAcquisition',
            'owner',
            'endOfWarranty',
            'comment'
        ]];

        const wb = utils.book_new();
        const ws: any = utils.json_to_sheet([]);

        utils.sheet_add_aoa(ws, header);

        const exportDevices: DeviceForExcel[] = [];

        this.sortedDevices.forEach(device => exportDevices.push(new DeviceForExcel(device)));

        utils.sheet_add_json(ws, exportDevices, {origin: 'A2', skipHeader: true});
        utils.book_append_sheet(wb, ws, 'Devices');
        writeFile(wb, 'raktr-devices.xlsx');
    }

    private getLocAndCat() {
        this.locationService.getLocations().subscribe(value => {
            this.locations = value;

            const locGroupItems = {};
            this.locations.forEach(location => {
                locGroupItems[location.id] = false;
            });

            this.locationGroup = this.formBuilder.group(locGroupItems);

            this.locationGroup.valueChanges.subscribe(groupItem => {
                switch (this.currentTab) {
                    case 'devices':
                        this.filterDevices();
                        break;
                    case 'composites':
                        this.filterCompositeItems();
                        break;
                }
            });
        });

        this.categoryService.getCategories().subscribe(value => {
            this.categories = value;

            const catGroupItems = {};
            this.categories.forEach(category => {
                catGroupItems[category.id] = false;
            });

            this.categoryGroup = this.formBuilder.group(catGroupItems);

            this.categoryGroup.valueChanges.subscribe(groupItem => {
                switch (this.currentTab) {
                    case 'devices':
                        this.filterDevices();
                        break;
                    case 'composites':
                        this.filterCompositeItems();
                        break;
                }
            });
        });
    }

    private allFilterOff(filters: Object) {
        const keys = Object.keys(filters);

        for (let i = 0; i < keys.length; i++) {
            if (filters[keys[i]]) {
                return false;
            }
        }

        return true;
    }

    private checkBoxFilter(relevantIdOfScannable: number, filterGroupValue: Object, allOfType: Object[]) {
        if (this.allFilterOff(filterGroupValue)) {
            return true;
        }

        let matchFound = false;

        allOfType.forEach(item => {
            if (filterGroupValue[item['id']]) {
                if (relevantIdOfScannable === item['id']) {
                    matchFound = true;
                }
            }
        });

        return matchFound;
    }

    private filterDevices() {
        const value = this.searchControl.value;

        this.sortedDevices = this.devices.filter(device =>
            (device.name.toLowerCase().includes(value.toLowerCase()) ||
                device.maker.toLowerCase().includes(value.toLowerCase()) ||
                device.type.toLowerCase().includes(value.toLowerCase()) ||
                device.location.name.toLowerCase().includes(value.toLowerCase()) ||
                device.category.name.toLowerCase().includes(value.toLowerCase()) ||
                device.textIdentifier.toLowerCase().includes(value.toLowerCase()) ||
                device.barcode.toLowerCase().includes(value.toLowerCase())) &&
            this.checkBoxFilter(device.location.id, this.locationGroup.value, this.locations) &&
            this.checkBoxFilter(device.category.id, this.categoryGroup.value, this.categories));

        this.setDevicePage();
    }

    private filterCompositeItems() {
        const value = this.searchControl.value;

        this.sortedComposites = this.compositeItems.filter(compositeItem =>
            (compositeItem.name.toLowerCase().includes(value) ||
                compositeItem.textIdentifier.toLowerCase().includes(value) ||
                compositeItem.barcode.toLowerCase().includes(value)) &&
            this.checkBoxFilter(compositeItem.location.id, this.locationGroup.value, this.locations) &&
            this.checkBoxFilter(compositeItem.category.id, this.categoryGroup.value, this.categories));

        this.setCompositePage();
    }

    deleteFilters() {
        const locGroupItems = {};
        this.locations.forEach(location => {
            locGroupItems[location.id] = false;
        });

        this.locationGroup.setValue(locGroupItems);

        const catGroupItems = {};
        this.categories.forEach(category => {
            catGroupItems[category.id] = false;
        });

        this.categoryGroup.setValue(catGroupItems);

        switch (this.currentTab) {
            case 'devices':
                this.setDevicePage();
                break;
            case 'composites':
                this.setCompositePage();
                break;
        }
    }

    public numOfLocation(loc: Location): number {
        switch (this.currentTab) {
            case 'devices':
                return this.devices.filter(device => device.location.name === loc.name).length;
            case 'composites':
                return this.compositeItems.filter(item => item.location.name === loc.name).length;
            default:
                return 0;
        }
    }

    public numOfCategory(cat: Category): number {
        switch (this.currentTab) {
            case 'devices':
                return this.devices.filter(device => device.category.name === cat.name).length;
            case 'composites':
                return this.compositeItems.filter(item => item.category.name === cat.name).length;
            default:
                return 0;
        }
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

    isMobile() {
        return $(window).width() <= 991;
    };

}

function compare(a: number | string, b: number | string, isAsc: boolean) {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
}
