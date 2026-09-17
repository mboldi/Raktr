import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {MatCard, MatCardContent, MatCardHeader} from '@angular/material/card';
import {MatButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatDivider} from '@angular/material/list';
import {MatChip} from '@angular/material/chips';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {DatePipe} from '@angular/common';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {HttpErrorResponse} from '@angular/common/http';
import {RentFormComponent} from '../../../components/rent-form/rent-form.component';
import {RentDetails} from '../../../model/rent/rentDetails';
import {RentCreateDto} from '../../../model/rent/rentCreateDto';
import {RentUpdateDto} from '../../../model/rent/rentUpdateDto';
import {RentItemCreateDto} from '../../../model/rent/rentItem/rentItemCreateDto';
import {RentItemUpdateDto} from '../../../model/rent/rentItem/rentItemUpdateDto';
import {RentItemDetailsDto} from '../../../model/rent/rentItem/rentItemDetails';
import {RentItemStatus} from '../../../model/rent/rentItem/rentItemStatus';
import {CommentCreateDto} from '../../../model/comment/commentCreateDto';
import {CommentDetailsDto} from '../../../model/comment/commentDetailsDto';
import {UserDetails} from '../../../model/user/userDetails';
import {RentService} from '../../../services/rent.service';
import {AdminAccessService} from '../../../services/adminAccess.service';
import {AddScannableEvent, ItemQuantityChangedEvent, ItemStatusChangedEvent} from '../../../components/rent-form/rent-form.component';
import {YesnoModalComponent} from '../../../components/yesno-modal/yesno-modal.component';

@Component({
  selector: 'app-edit-rent',
  imports: [
    RouterLink,
    MatCard,
    MatCardHeader,
    MatCardContent,
    MatButton,
    MatIcon,
    MatFormField,
    MatLabel,
    MatInput,
    MatDivider,
    MatChip,
    MatProgressSpinner,
    DatePipe,
    ReactiveFormsModule,
    RentFormComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './edit-rent.component.html',
  styleUrl: './edit-rent.component.scss',
})
export class EditRentComponent implements OnInit {
  @ViewChild(RentFormComponent) rentFormComponent!: RentFormComponent;

  protected loading = true;
  protected isNew = true;
  protected rent: RentDetails | null = null;

  protected currentUser: UserDetails | null = null;
  protected isAdmin = false;
  protected isFullAccessMember = false;

  protected newCommentControl = new FormControl('');

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private rentService: RentService,
    private adminAccessService: AdminAccessService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef,
  ) {
  }

  ngOnInit(): void {
    this.adminAccessService.getCurrentUser().subscribe(user => {
      this.currentUser = user;
      this.cdr.markForCheck();
    });
    this.adminAccessService.isAdmin().subscribe(isAdmin => {
      this.isAdmin = isAdmin;
      this.cdr.markForCheck();
    });
    this.adminAccessService.isFullAccessMember().subscribe(isFullAccess => {
      this.isFullAccessMember = isFullAccess;
      this.cdr.markForCheck();
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam === null) {
      this.isNew = true;
      this.loading = false;
      return;
    }

    this.isNew = false;
    this.rentService.getRent(+idParam).subscribe({
      next: rent => {
        this.rent = rent;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.router.navigateByUrl('/rents');
        this.snackBar.open(`Nincs kivitel ${idParam} azonosítóval!`, 'Kár :(', {
          duration: 4000,
          horizontalPosition: 'right',
          verticalPosition: 'top',
          panelClass: ['error-snackbar'],
        });
      }
    });
  }

  protected get sortedComments(): CommentDetailsDto[] {
    return [...(this.rent?.comments ?? [])].sort((a, b) => b.createdAt.getTime() - a.createdAt.getTime());
  }

  /** Closed rents are read-only for everyone except admins, who can still fix mistakes
   * (there is no manual "reopen" - a rent closes automatically once every item is back). */
  protected get isReadOnly(): boolean {
    return (this.rent?.closed ?? false) && !this.isAdmin;
  }

  protected get isFormValid(): boolean {
    return this.rentFormComponent?.rentForm?.valid ?? false;
  }

  private refreshRent() {
    if (this.rent === null) {
      return;
    }

    this.rentService.getRent(this.rent.id).subscribe(rent => {
      this.rent = rent;
      this.cdr.markForCheck();
    });
  }

  private notify(message: string, panelClass: string, actionLabel = 'Rendben') {
    this.snackBar.open(message, actionLabel, {
      duration: 3000,
      horizontalPosition: 'right',
      verticalPosition: 'top',
      panelClass: [panelClass],
    });
  }

  protected save() {
    if (!this.isFormValid) {
      this.rentFormComponent?.markAllFieldsAsTouched();
      this.notify('Tölts ki minden kötelező mezőt!', 'error-snackbar', "Let's do it!");
      return;
    }

    const formValue = this.rentFormComponent.rentForm.getRawValue();
    const issuerId: string = formValue.issuer.uuid;

    if (this.isNew) {
      const newRent = new RentCreateDto(
        formValue.type,
        formValue.destination,
        issuerId,
        formValue.renterName,
        formValue.outDate,
        formValue.expectedReturnDate,
      );

      this.rentService.createRent(newRent).subscribe(createdRent => {
        this.rent = createdRent;
        this.isNew = false;

        this.notify('Kivitel létrehozva!', 'success-snackbar', 'Remek!');

        this.router.navigate(['/rents', createdRent.id]);
      });
    } else {
      const updatedRent = new RentUpdateDto(
        formValue.destination,
        issuerId,
        formValue.renterName,
        formValue.outDate,
        formValue.expectedReturnDate,
        formValue.actualReturnDate || null,
      );

      this.rentService.updateRent(this.rent!.id, updatedRent).subscribe(rent => {
        this.rent = rent;

        this.notify('Kivitel mentve!', 'success-snackbar', 'Remek!');
      });
    }
  }

  protected onAddScannable({scannable, quantity}: AddScannableEvent) {
    if (!this.rent) {
      return;
    }

    this.rentService.addRentItem(this.rent.id, new RentItemCreateDto(scannable.id, quantity)).subscribe({
      next: () => {
        this.refreshRent();
        this.notify(`${scannable.name} hozzáadva a kivitelhez!`, 'success-snackbar', 'Remek!');
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 409) {
          this.notify(`A(z) ${scannable.name} már hozzá van adva a kivitelhez!`, 'error-snackbar');
        } else {
          this.notify(`Nem sikerült hozzáadni a(z) ${scannable.name} eszközt!`, 'error-snackbar');
        }
      }
    });
  }

  protected onScannableNotFound() {
    this.notify('Nem található eszköz ezzel a vonalkóddal!', 'error-snackbar');
  }

  protected onScannableNotOnRent() {
    this.notify('Ez az eszköz még nincs hozzáadva a kivitelhez!', 'error-snackbar');
  }

  protected onRemoveItem(item: RentItemDetailsDto) {
    if (!this.rent) {
      return;
    }

    this.rentService.deleteRentItem(this.rent.id, item.id).subscribe(() => {
      this.refreshRent();
      this.notify(`${item.scannable.name} eltávolítva a kivitelből!`, 'success-snackbar', 'Rendben');
    });
  }

  protected onItemQuantityChanged({item, quantity}: ItemQuantityChangedEvent) {
    this.updateItem(item, item.status, quantity, `${item.scannable.name} mennyisége frissítve: ${quantity} db`);
  }

  protected onItemStatusChanged({item, status}: ItemStatusChangedEvent) {
    this.updateItem(item, status, item.quantity, 'Sikeresen mentve!');
  }

  private updateItem(item: RentItemDetailsDto, status: RentItemStatus, quantity: number, successMessage: string) {
    if (!this.rent) {
      return;
    }

    this.rentService.updateRentItem(this.rent.id, item.id, new RentItemUpdateDto(status, quantity)).subscribe({
      next: () => {
        this.refreshRent();
        this.notify(successMessage, 'success-snackbar', 'Remek!');
      },
      error: () => {
        this.refreshRent();
        this.notify('Nem sikerült menteni :(', 'error-snackbar');
      }
    });
  }

  protected deleteRent() {
    if (!this.rent) {
      return;
    }

    const confirmDialog = this.dialog.open(YesnoModalComponent, {
      width: '20vw',
      minWidth: '350px',
      data: `Biztos törlöd a(z) "${this.rent.destination}" kivitelt?`
    });

    confirmDialog.afterClosed().subscribe(result => {
      if (result && this.rent) {
        this.rentService.deleteRent(this.rent.id).subscribe(() => {
          this.notify('Kivitel törölve!', 'success-snackbar', 'Rendben');
          this.router.navigateByUrl('/rents');
        });
      }
    });
  }

  protected sendComment() {
    const body = this.newCommentControl.value;
    if (!body || !this.rent) {
      return;
    }

    this.rentService.addComment(this.rent.id, new CommentCreateDto(body)).subscribe(comment => {
      this.rent!.comments = [...this.rent!.comments, comment];
      this.newCommentControl.reset('');
      this.cdr.markForCheck();
    });
  }
}
