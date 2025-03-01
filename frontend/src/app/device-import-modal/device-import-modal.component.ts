import {Component, Input, OnInit} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {read, Sheet, utils, WorkBook} from 'xlsx';
import {DeviceService} from '../_services/device.service';
import {Device} from '../_model/Device';
import {Category} from '../_model/Category';
import {Location} from '../_model/Location';
import {ScannableService} from '../_services/scannable.service';
import {error} from 'protractor';

@Component({
    selector: 'app-device-import-modal',
    templateUrl: './device-import-modal.component.html',
    styleUrls: ['./device-import-modal.component.css']
})
export class DeviceImportModalComponent implements OnInit {
    @Input() importFile;

    importInProgress = false;
    handledDevices = 0;
    successfulHandles = 0;
    importMessages: Object[] = [];

    reader = new FileReader();
    wb: WorkBook;
    firstSheet: Sheet;
    firstSheetRows: Object[];

    demoRows: Object[] = [];

    constructor(public activeModal: NgbActiveModal,
                private deviceService: DeviceService,
                private scannableService: ScannableService) {
        this.reader.onload = (event: any) => {
            this.wb = read(event.target.result);
            const sheets = this.wb.SheetNames;
            this.firstSheet = this.wb.Sheets[sheets[0]];

            if (sheets.length) {
                this.firstSheetRows = utils.sheet_to_json(this.firstSheet);

                if (this.firstSheetRows.length <= 5) {
                    this.demoRows = this.firstSheetRows;
                } else {
                    this.demoRows = this.firstSheetRows.slice(0, 5);
                }
            }

        }
    }

    ngOnInit(): void {
        this.reader.readAsArrayBuffer(this.importFile);
    }

    protected doImport() {
        this.importInProgress = true;

        this.firstSheetRows.forEach(row => {
            if (row['name'] === undefined || row['name'] === null || row['name'] === '') {
                this.importMessages.push({'row': row['__rowNum__'], 'status': 'error', 'message': 'Hiányzó név'});

                this.handledDevices++;
                return;
            }

            if (row['textIdentifier'] === undefined || row['textIdentifier'] === null || row['textIdentifier'] === '') {
                this.importMessages.push({'row': row['__rowNum__'], 'status': 'error', 'message': 'Hiányzó szöveges azonosító'});

                this.handledDevices++;
                return;
            }

            if (row['barcode'] === undefined || row['barcode'] === null || row['barcode'] === '') {
                this.importMessages.push({'row': row['__rowNum__'], 'status': 'error', 'message': 'Hiányzó vonalkód'});

                this.handledDevices++;
                return;
            }

            if (row['category'] === undefined || row['category'] === null || row['category'] === '') {
                this.importMessages.push({'row': row['__rowNum__'], 'status': 'error', 'message': 'Hiányzó kategória'});

                this.handledDevices++;
                return;
            }

            if (row['location'] === undefined || row['location'] === null || row['location'] === '') {
                this.importMessages.push({'row': row['__rowNum__'], 'status': 'error', 'message': 'Hiányzó tárolási hely'});

                this.handledDevices++;
                return;
            }

            this.scannableService.getScannableByBarcode(row['barcode']).subscribe(result => {
                if (result !== null && result.id !== row['id']) {
                    this.importMessages.push({'row': row['__rowNum__'], 'status': 'error', 'message': 'Vonalkód foglalt'});

                    this.handledDevices++;
                    return;
                }

                this.saveDevice(row);

            }, error => {
                this.scannableService.getScannableByTextIdentifier(row['textIdentifier']).subscribe(resultByTextId => {
                    if (resultByTextId !== null && resultByTextId.id !== row['id']) {
                        this.importMessages.push({'row': row['__rowNum__'], 'status': 'error', 'message': 'Szöveges azonosító foglalt'});

                        this.handledDevices++;
                        return;
                    }

                    this.saveDevice(row);

                }, error1 => {
                    this.saveDevice(row);
                });
            })


        })
    }

    private saveDevice(row: Object) {
        const actDevice = new Device(
            row['id'],
            row['name'],
            row['barcode'],
            row['textIdentifier'],
            row['isPublicRentable'],
            row['maker'],
            row['type'],
            row['serial'],
            row['value'],
            row['weight'],
            new Location(-1, row['location']),
            row['status'],
            new Category(-1, row['category']),
            row['quantity'],
            row['acquiredFrom'],
            row['dateOfAcquisition'] === '' ? null : new Date(row['dateOfAcquisition']),
            row['owner'] === '' ? null : row['owner'],
            row['endOfWarranty'] === '' ? null : new Date(row['endOfWarranty']),
            row['comment']
        )

        this.deviceService.updateDevice(actDevice).subscribe(deviceResult => {
            this.handledDevices++;
            this.successfulHandles++;
            this.importMessages.push({
                'row': row['__rowNum__'],
                'status': 'success',
                'message': 'Sikeresen importálva: ' + deviceResult.name
            });
        }, error4 => {
            this.handledDevices++;
            this.importMessages.push({
                'row': row['__rowNum__'],
                'status': 'error',
                'message': 'Ismeretlen hiba'
            });
        });
    }

    exit() {
        this.activeModal.dismiss();
    }
}
