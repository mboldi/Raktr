import {
  ChangeDetectionStrategy,
  Component,
  computed,
  effect,
  input,
  OnInit,
  output,
  ViewChild,
  inject,
} from '@angular/core';
import {
  MatFormField,
  MatInput,
  MatInputModule,
  MatLabel,
  MatSuffix,
} from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocomplete, MatAutocompleteTrigger, MatOption } from '@angular/material/autocomplete';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSortModule, Sort } from '@angular/material/sort';
import {
  FormBuilder,
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { map, Observable, startWith } from 'rxjs';
import { AsyncPipe, DatePipe, DecimalPipe } from '@angular/common';
import { MatSelect } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import {
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle,
} from '@angular/material/expansion';
import { MatTooltip } from '@angular/material/tooltip';
import { MatDialog } from '@angular/material/dialog';
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
  MatTable,
} from '@angular/material/table';
import { RentDetails } from '../../model/rent/rentDetails';
import { RentType } from '../../model/rent/rentType';
import { RentItemStatus } from '../../model/rent/rentItem/rentItemStatus';
import { RentItemDetailsDto } from '../../model/rent/rentItem/rentItemDetails';
import { RENT_TYPE_LABELS } from '../../model/rent/rentLabels';
import { ScannableDetailsDto } from '../../model/scannable/scannableDetailsDto';
import { DeviceDetails } from '../../model/scannable/device/deviceDetails';
import { ContainerDetails } from '../../model/scannable/container/containerDetails';
import { DeviceService } from '../../services/device.service';
import { ContainerService } from '../../services/container.service';
import { UserDetails } from '../../model/user/userDetails';
import { UserService } from '../../services/user.service';
import { findByBarcode } from '../../util/ean8';
import {
  QuantityInputDialogData,
  QuantityInputModalComponent,
} from '../quantity-input-modal/quantity-input-modal.component';
import { YesnoModalComponent } from '../yesno-modal/yesno-modal.component';
import { WindowWidthService } from '../../services/windowWidth.service';

const MOBILE_WIDTH_THRESHOLD = 768;

export interface AddScannableEvent {
  scannable: ScannableDetailsDto;
  quantity: number;
}

export interface ItemQuantityChangedEvent {
  item: RentItemDetailsDto;
  quantity: number;
}

export interface ItemStatusChangedEvent {
  item: RentItemDetailsDto;
  status: RentItemStatus;
}

/** Which checkbox column the search/scan box currently targets - null means it adds new items instead. */
export type StatusMode = 'packed' | 'returned';

@Component({
  selector: 'app-rent-form',
  imports: [
    MatFormField,
    MatLabel,
    MatInput,
    MatInputModule,
    MatFormFieldModule,
    MatSuffix,
    MatAutocomplete,
    MatAutocompleteTrigger,
    MatOption,
    ReactiveFormsModule,
    FormsModule,
    AsyncPipe,
    DecimalPipe,
    DatePipe,
    MatSelect,
    MatDatepickerModule,
    MatIcon,
    MatIconModule,
    MatIconButton,
    MatCheckbox,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatTooltip,
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatCellDef,
    MatHeaderCellDef,
    MatHeaderRow,
    MatRow,
    MatHeaderRowDef,
    MatRowDef,
    MatButton,
    MatPaginator,
    MatSortModule,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './rent-form.component.html',
  styleUrl: './rent-form.component.scss',
})
export class RentFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private deviceService = inject(DeviceService);
  private containerService = inject(ContainerService);
  private dialog = inject(MatDialog);
  private windowService = inject(WindowWidthService);

  @ViewChild(MatTable) itemsTable?: MatTable<unknown>;

  /** Pass an existing rent to pre-populate the form, or leave null for a blank create form. */
  rentData = input<RentDetails | null>(null);

  /** Disables every field, e.g. because the rent is closed and the user isn't an admin. */
  disabled = input<boolean>(false);

  /** The user to pre-select as issuer when creating a new rent. */
  defaultIssuer = input<UserDetails | null>(null);

  /** Shows the delete button next to the save button, e.g. because the current user is an admin. */
  canDelete = input<boolean>(false);

  /** Emits the latest raw form value whenever the user makes a change. */
  formChanged = output<Partial<RentDetails>>();

  /** Emits the resolved scannable (and chosen quantity) to add once a barcode is matched or an autocomplete option is picked. */
  addScannable = output<AddScannableEvent>();
  /** Emitted when the entered value doesn't match any known device's or container's barcode. */
  scannableNotFound = output<void>();
  /** Emitted when a status mode is active (see `selectedStatusMode`) but the resolved scannable isn't on this rent yet. */
  scannableNotOnRent = output<void>();
  /** Emits the item to remove from the rent. */
  removeItem = output<RentItemDetailsDto>();
  /** Emits when a device item's taken-out quantity is edited. */
  itemQuantityChanged = output<ItemQuantityChangedEvent>();
  /** Emits when an item's packed/returned checkboxes change its status. */
  itemStatusChanged = output<ItemStatusChangedEvent>();
  /** Emits when the user clicks the save button for the basic rent data. */
  save = output<void>();
  /** Emits when the user clicks the delete button. */
  deleteRent = output<void>();

  rentForm: UntypedFormGroup;

  protected readonly RentType = RentType;
  protected readonly RentItemStatus = RentItemStatus;
  protected readonly rentTypeLabels = RENT_TYPE_LABELS;

  protected issuers: UserDetails[] = [];
  protected filteredIssuers: Observable<UserDetails[]>;

  protected addScannableFormControl = new FormControl('');
  /** When set by clicking a checkbox column's header, the search/scan box no longer adds new
   * items - it looks up an item already on the rent and flips that column's status on it instead. */
  protected selectedStatusMode: StatusMode | null = null;
  protected devices: DeviceDetails[] = [];
  protected containers: ContainerDetails[] = [];
  protected devicesById = new Map<number, DeviceDetails>();
  protected containersById = new Map<number, ContainerDetails>();
  protected filteredNewScannableOptions: Observable<ScannableDetailsDto[]>;

  protected itemSearchControl = new FormControl('');
  protected filteredItems: RentItemDetailsDto[] = [];
  protected pagedItems: RentItemDetailsDto[] = [];
  protected itemsPageSize = 5;
  private itemsPageIndex = 0;
  private itemsSort: Sort = { active: 'addedBy', direction: 'desc' };

  /** Recomputed reactively off both the rent (type/closed) and the viewport width. */
  protected readonly itemColumns = computed(() => this.computeItemColumns(this.rentData()));

  constructor() {
    this.rentForm = this.fb.group({
      destination: ['', Validators.required],
      type: [RentType.SIMPLE, Validators.required],
      issuer: ['', Validators.required],
      renterName: ['', Validators.required],
      outDate: [new Date(), Validators.required],
      expectedReturnDate: ['', Validators.required],
      actualReturnDate: [''],
    });

    this.filteredIssuers = this.rentForm.get('issuer')!.valueChanges.pipe(
      startWith(''),
      map((value) => this.filterIssuers(value || '')),
    );

    this.filteredNewScannableOptions = this.addScannableFormControl.valueChanges.pipe(
      startWith(''),
      map((value) => this.filterScannables(value || '')),
    );

    // The rent type decides how items are tracked (SIMPLE vs. COMPLEX packing/return steps),
    // so once items may already exist against it, it's locked - changing it afterwards would
    // leave existing items' statuses inconsistent with the new type's rules.
    effect(() => {
      const isNew = this.isNew;

      if (this.disabled()) {
        this.rentForm.disable({ emitEvent: false });
      } else {
        this.rentForm.enable({ emitEvent: false });

        if (!isNew) {
          this.rentForm.get('type')!.disable({ emitEvent: false });
        }
      }
    });

    // The items table binds [dataSource] to rentData().rentItems, which is a plain array -
    // when the host page swaps in a fresh rent after add/remove/status changes, CdkTable
    // doesn't always notice on its own, so force it to redraw and refilter every time.
    effect(() => {
      this.rentData();
      this.refilterItems();
      this.itemsTable?.renderRows();
    });

    // The host page auto-fills the actual return date once every item is back (see
    // EditRentComponent) and swaps in the updated rent - reflect that here too, but only while
    // the field is still pristine, so it doesn't clobber a date the user is already editing by hand.
    effect(() => {
      const rent = this.rentData();
      const control = this.rentForm.get('actualReturnDate')!;
      if (rent && control.pristine) {
        control.setValue(rent.actualReturnDate, { emitEvent: false });
      }
    });
  }

  protected get isNew(): boolean {
    return this.rentData() === null;
  }

  /** Once a rent is closed, its items are frozen - this is checked directly against the rent
   * (unlike the `disabled` input, which also factors in the current user being an admin, since
   * admins may still fix the basic rent data on a closed rent but items no longer make sense to touch). */
  protected get itemsLocked(): boolean {
    return this.rentData()?.closed ?? false;
  }

  private get scannables(): ScannableDetailsDto[] {
    return [...this.devices, ...this.containers];
  }

  ngOnInit(): void {
    const data = this.rentData();
    if (data !== null) {
      this.rentForm.patchValue({
        destination: data.destination,
        type: data.type,
        issuer: data.issuer,
        renterName: data.renterName,
        outDate: data.outDate,
        expectedReturnDate: data.expectedReturnDate,
        actualReturnDate: data.actualReturnDate,
      });
    }

    this.rentForm.valueChanges.subscribe((value) => this.formChanged.emit(value));

    this.itemSearchControl.valueChanges.subscribe(() => this.refilterItems());

    this.userService.getUsers(true).subscribe((issuers) => {
      this.issuers = issuers;

      if (data === null) {
        const defaultIssuer = this.defaultIssuer();
        if (defaultIssuer) {
          this.rentForm.get('issuer')!.setValue(defaultIssuer);
          return;
        }
      }

      this.rentForm.get('issuer')!.updateValueAndValidity();
    });

    this.deviceService.getDevices().subscribe((devices) => {
      this.devices = devices;
      this.devicesById = new Map(devices.map((device) => [device.id, device]));
      this.addScannableFormControl.updateValueAndValidity();
    });

    this.containerService.getContainers().subscribe((containers) => {
      this.containers = containers;
      this.containersById = new Map(containers.map((container) => [container.id, container]));
      this.addScannableFormControl.updateValueAndValidity();
    });
  }

  private computeItemColumns(rent: RentDetails | null): string[] {
    const isMobile = this.windowService.windowWidth() < MOBILE_WIDTH_THRESHOLD;

    const columns = ['type', 'name', 'assetTag', 'quantity'];

    if (!isMobile) {
      columns.push('addedBy', 'weight');
    }

    if (rent?.type === RentType.COMPLEX) {
      columns.push('packed');
    }
    columns.push('returned');

    if (!isMobile && !this.itemsLocked) {
      columns.push('remove');
    }

    return columns;
  }

  protected displayIssuer(issuer: UserDetails | string | null): string {
    if (!issuer) return '';
    if (typeof issuer === 'string') return issuer;
    return `${issuer.familyName} ${issuer.givenName}`;
  }

  private filterIssuers(value: UserDetails | string): UserDetails[] {
    const filter = (
      typeof value === 'string' ? value : `${value.familyName} ${value.givenName}`
    ).toLowerCase();
    return this.issuers
      .filter((issuer) => `${issuer.familyName} ${issuer.givenName}`.toLowerCase().includes(filter))
      .slice(0, 10);
  }

  private filterScannables(value: string): ScannableDetailsDto[] {
    const filter = value.toLowerCase();
    if (filter.length < 2) {
      return [];
    }

    // In a status mode, the box helps find an item already on the rent to flip a checkbox on -
    // searching the addable scannables wouldn't make sense there.
    if (this.selectedStatusMode) {
      return (this.rentData()?.rentItems ?? [])
        .map((item) => item.scannable)
        .filter(
          (scannable) =>
            scannable.name.toLowerCase().includes(filter) ||
            scannable.assetTag.toLowerCase().includes(filter) ||
            (scannable instanceof DeviceDetails && scannable.model.toLowerCase().includes(filter)),
        )
        .slice(0, 50);
    }

    const addedIds = new Set((this.rentData()?.rentItems ?? []).map((item) => item.scannable.id));

    return this.scannables
      .filter(
        (scannable) =>
          // Devices with more than one in stock stay searchable even once added, so more can be
          // taken out - anything else (containers, single-quantity devices) drops off the list.
          (!addedIds.has(scannable.id) || this.isStackable(scannable.id)) &&
          !scannable.deleted &&
          (scannable.name.toLowerCase().includes(filter) ||
            scannable.assetTag.toLowerCase().includes(filter) ||
            (scannable instanceof DeviceDetails && scannable.model.toLowerCase().includes(filter))),
      )
      .slice(0, 50);
  }

  protected toggleStatusMode(mode: StatusMode) {
    if (this.itemsLocked) {
      return;
    }

    this.selectedStatusMode = this.selectedStatusMode === mode ? null : mode;
    this.addScannableFormControl.reset();
  }

  private isStackable(scannableId: number): boolean {
    const device = this.devicesById.get(scannableId);
    return !!device && device.quantity > 1;
  }

  protected isDevice(scannableId: number): boolean {
    return this.devicesById.has(scannableId);
  }

  private containerContentsTooltip(scannableId: number): string | null {
    const container = this.containersById.get(scannableId);
    if (!container) {
      return null;
    }

    if (container.items.length === 0) {
      return 'Üres láda';
    }

    const contents = container.items
      .map((item) => `    ${item.device.name} - ${item.device.model} - ${item.device.assetTag}`)
      .join('\n');

    return `Tartalmazott eszközök:\n${contents}`;
  }

  protected itemWeight(item: RentItemDetailsDto): number {
    return RentDetails.getItemWeight(item, this.containersById);
  }

  protected rentSumWeight(rent: RentDetails): number {
    return rent.getSumWeight(this.containersById);
  }

  protected hoveredContainerTooltip: string | null = null;
  protected containerTooltipPosition = { x: 0, y: 0 };

  protected onItemRowMouseEnter(item: RentItemDetailsDto, event: MouseEvent) {
    this.hoveredContainerTooltip = this.isDevice(item.scannable.id)
      ? null
      : this.containerContentsTooltip(item.scannable.id);
    this.onItemRowMouseMove(event);
  }

  protected onItemRowMouseMove(event: MouseEvent) {
    this.containerTooltipPosition = { x: event.clientX + 16, y: event.clientY + 16 };
  }

  protected onItemRowMouseLeave() {
    this.hoveredContainerTooltip = null;
  }

  private findExistingItem(scannableId: number): RentItemDetailsDto | undefined {
    return this.rentData()?.rentItems.find((item) => item.scannable.id === scannableId);
  }

  // MatAutocomplete's Enter-to-select handling runs on keydown and fires (optionSelected),
  // but the same keypress still produces a native keyup afterward that also hits our own
  // (keyup.enter) handler - this flag stops that from adding the scannable a second time.
  private justSelectedFromDropdown = false;

  protected onScannableOptionSelected() {
    this.justSelectedFromDropdown = true;
    this.resolveAndAddScannable();
  }

  protected onAddScannableEnterKey() {
    if (this.justSelectedFromDropdown) {
      this.justSelectedFromDropdown = false;
      return;
    }

    this.resolveAndAddScannable();
  }

  protected resolveAndAddScannable() {
    const enteredValue: string = this.addScannableFormControl.value ?? '';
    if (!enteredValue) {
      return;
    }

    const matched = findByBarcode(this.scannables, (scannable) => scannable.barcode, enteredValue);
    if (!matched) {
      this.scannableNotFound.emit();
      return;
    }

    if (this.selectedStatusMode) {
      this.applyStatusModeToScannable(matched);
      return;
    }

    const device = this.devicesById.get(matched.id);
    const existingItem = this.findExistingItem(matched.id);

    if (device && device.quantity > 1) {
      const quantityDialog = this.dialog.open(QuantityInputModalComponent, {
        width: '20vw',
        minWidth: '350px',
        data: new QuantityInputDialogData(
          device.name,
          device.quantity,
          existingItem?.quantity ?? 1,
        ),
      });

      quantityDialog.afterClosed().subscribe((chosenQuantity) => {
        if (chosenQuantity) {
          if (existingItem) {
            this.itemQuantityChanged.emit({ item: existingItem, quantity: chosenQuantity });
          } else {
            this.addScannable.emit({ scannable: matched, quantity: chosenQuantity });
          }
          this.addScannableFormControl.reset();
        }
      });
    } else {
      this.addScannable.emit({ scannable: matched, quantity: 1 });
      this.addScannableFormControl.reset();
    }
  }

  private applyStatusModeToScannable(scannable: ScannableDetailsDto) {
    const item = this.findExistingItem(scannable.id);

    if (!item) {
      this.scannableNotOnRent.emit();
      this.addScannableFormControl.reset();
      return;
    }

    if (this.selectedStatusMode === 'packed') {
      this.togglePacked(item);
    } else {
      this.processReturnToggle(item);
    }

    this.addScannableFormControl.reset();
  }

  private refilterItems() {
    const search = (this.itemSearchControl.value ?? '').toLowerCase();
    const items = this.rentData()?.rentItems ?? [];

    this.filteredItems = items.filter(
      (item) =>
        item.scannable.name.toLowerCase().includes(search) ||
        item.scannable.barcode.toLowerCase().includes(search) ||
        item.scannable.assetTag.toLowerCase().includes(search) ||
        item.scannable.category.toLowerCase().includes(search) ||
        item.scannable.location.toLowerCase().includes(search),
    );

    this.sortItems();
    this.itemsPageIndex = 0;
    this.pageItems();
  }

  protected onItemsSortChange(sort: Sort) {
    this.itemsSort = sort;
    this.sortItems();
    this.itemsPageIndex = 0;
    this.pageItems();
  }

  private sortItems() {
    const { active, direction } = this.itemsSort;
    const comparator = direction ? this.getItemsSortComparator(active) : null;
    if (!comparator) {
      return;
    }

    this.filteredItems = this.filteredItems
      .slice()
      .sort((a, b) => (direction === 'asc' ? comparator(a, b) : -comparator(a, b)));
  }

  private getItemsSortComparator(
    active: string,
  ): ((a: RentItemDetailsDto, b: RentItemDetailsDto) => number) | null {
    switch (active) {
      case 'name':
        return (a, b) => a.scannable.name.localeCompare(b.scannable.name);
      case 'assetTag':
        return (a, b) => a.scannable.assetTag.localeCompare(b.scannable.assetTag);
      case 'addedBy':
        // Sorted by the timestamp only - the added-by person's name isn't part of the sort.
        return (a, b) => a.createdAt.getTime() - b.createdAt.getTime();
      default:
        return null;
    }
  }

  protected onItemsPage(event: PageEvent) {
    this.itemsPageIndex = event.pageIndex;
    this.itemsPageSize = event.pageSize;
    this.pageItems();
  }

  private pageItems() {
    const start = this.itemsPageIndex * this.itemsPageSize;
    this.pagedItems = this.filteredItems.slice(start, start + this.itemsPageSize);
  }

  protected isAmountEditable(item: RentItemDetailsDto): boolean {
    const device = this.devicesById.get(item.scannable.id);
    return !!device && device.quantity > 1;
  }

  protected onQuantityChanged(event: Event, item: RentItemDetailsDto) {
    const input = event.target as HTMLInputElement;
    const newQuantity = Number(input.value);
    const device = this.devicesById.get(item.scannable.id);

    if (!device || newQuantity < 1 || newQuantity > device.quantity) {
      input.value = item.quantity.toString();
      return;
    }

    this.itemQuantityChanged.emit({ item, quantity: newQuantity });
  }

  protected onPackedChanged(checkboxChange: MatCheckboxChange, item: RentItemDetailsDto) {
    if (!this.togglePacked(item)) {
      checkboxChange.source.checked = true;
    }
  }

  protected onReturnedChanged(checkboxChange: MatCheckboxChange, item: RentItemDetailsDto) {
    const checkbox = checkboxChange.source;
    this.processReturnToggle(
      item,
      () => (checkbox.checked = false),
      () => (checkbox.checked = true),
    );
  }

  /** Returns false (and leaves the item untouched) when it's already returned - packing can't be undone at that point. */
  private togglePacked(item: RentItemDetailsDto): boolean {
    if (item.status === RentItemStatus.RETURNED) {
      return false;
    }

    const newStatus =
      item.status === RentItemStatus.PENDING ? RentItemStatus.OUT : RentItemStatus.PENDING;
    this.itemStatusChanged.emit({ item, status: newStatus });
    return true;
  }

  /** Un-returns immediately; marking returned instead calls `onBlocked` (COMPLEX rent, not
   * packed yet) or, for items rented in more than one unit, opens a confirmation dialog first
   * and calls `onConfirmed` once the full quantity is confirmed as returned. */
  private processReturnToggle(
    item: RentItemDetailsDto,
    onBlocked?: () => void,
    onConfirmed?: () => void,
  ) {
    if (item.status === RentItemStatus.RETURNED) {
      this.itemStatusChanged.emit({ item, status: RentItemStatus.OUT });
      return;
    }

    const rent = this.rentData();
    if (rent?.type === RentType.COMPLEX && item.status !== RentItemStatus.OUT) {
      onBlocked?.();
      return;
    }

    if (item.quantity > 1) {
      onBlocked?.();
      this.confirmFullReturn(item).subscribe((confirmed) => {
        if (confirmed) {
          onConfirmed?.();
          this.itemStatusChanged.emit({ item, status: RentItemStatus.RETURNED });
        }
      });
      return;
    }

    this.itemStatusChanged.emit({ item, status: RentItemStatus.RETURNED });
  }

  private confirmFullReturn(item: RentItemDetailsDto): Observable<boolean> {
    const dialogRef = this.dialog.open(QuantityInputModalComponent, {
      width: '20vw',
      minWidth: '350px',
      data: new QuantityInputDialogData(
        item.scannable.name,
        item.quantity,
        item.quantity,
        item.quantity,
        `${item.scannable.name} - mind a(z) ${item.quantity} darab visszakerült?`,
        'Visszahozás',
      ),
    });

    return dialogRef.afterClosed().pipe(map((result) => !!result));
  }

  protected canRemove(item: RentItemDetailsDto): boolean {
    if (this.rentData()?.type === RentType.COMPLEX) {
      return item.status === RentItemStatus.PENDING;
    }

    return item.status !== RentItemStatus.RETURNED;
  }

  protected removeFromRent(item: RentItemDetailsDto) {
    const confirmDialog = this.dialog.open(YesnoModalComponent, {
      width: '20vw',
      minWidth: '350px',
      data: `Biztos eltávolítod a(z) ${item.scannable.name} eszközt a kivitelből?`,
    });

    confirmDialog.afterClosed().subscribe((result) => {
      if (result) {
        this.removeItem.emit(item);
      }
    });
  }

  /** Marks every field as touched so Material shows the invalid ones highlighted, as if the user had visited them. */
  public markAllFieldsAsTouched() {
    this.rentForm.markAllAsTouched();
  }
}
