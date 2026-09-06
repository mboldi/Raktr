import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, ViewChild} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {MatSlideToggle} from '@angular/material/slide-toggle';
import {MatDivider} from '@angular/material/list';
import {MatChip} from '@angular/material/chips';
import {DatePipe} from '@angular/common';
import {filter} from 'rxjs';
import {TicketFormComponent} from '../ticket-form/ticket-form.component';
import {TicketDetails} from '../../model/ticket/ticketDetails';
import {TicketCreateDto} from '../../model/ticket/ticketCreateDto';
import {TicketUpdateDto} from '../../model/ticket/ticketUpdateDto.ty';
import {TicketStatus} from '../../model/ticket/ticketStatus';
import {CommentCreateDto} from '../../model/comment/commentCreateDto';
import {CommentDetailsDto} from '../../model/comment/commentDetailsDto';
import {TicketService} from '../../services/ticket.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {AdminAccessService} from '../../services/adminAccess.service';

export interface TicketDialogResult {
  ticket: TicketDetails;
  /** True only when the user explicitly pressed "Mentés" - false for a plain exit that merely
   * picked up comment-triggered changes, so the caller can skip the "saved" confirmation then. */
  saved: boolean;
}

@Component({
  selector: 'app-ticket-edit-modal',
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle,
    TicketFormComponent,
    MatFormField,
    MatLabel,
    MatInput,
    ReactiveFormsModule,
    MatSlideToggle,
    MatDivider,
    MatChip,
    DatePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './ticket-edit-dialog.component.html',
  styleUrl: './ticket-edit-dialog.component.scss',
})
export class TicketEditDialogComponent {
  @ViewChild(TicketFormComponent) ticketFormComponent!: TicketFormComponent;

  protected title = 'Új hibajegy';
  protected isNew: boolean = true;
  protected ticket: TicketDetails | null;

  protected newCommentControl = new FormControl('');
  protected closingComment = false;
  protected isAdmin = false;

  protected readonly TicketStatus = TicketStatus;

  constructor(@Inject(MAT_DIALOG_DATA) protected ticketData: TicketDetails,
              private dialogRef: MatDialogRef<TicketEditDialogComponent>,
              private snackBar: MatSnackBar,
              private ticketService: TicketService,
              private adminAccessService: AdminAccessService,
              private cdr: ChangeDetectorRef) {
    this.ticket = ticketData ?? null;

    if (ticketData) {
      this.isNew = false;
      this.title = `Hibajegy szerkesztése - ${ticketData.id}`;
    }

    this.adminAccessService.isAdmin().subscribe(isAdmin => {
      this.isAdmin = isAdmin;
      this.cdr.markForCheck();
    });

    // Comments (and the status changes they trigger) are already saved via their own API
    // calls, so closing the dialog any way - backdrop click, Escape, or "Vissza" - should
    // still report the latest ticket state back to the list, not just an explicit save.
    this.dialogRef.disableClose = true;
    this.dialogRef.backdropClick().subscribe(() => this.close());
    this.dialogRef.keydownEvents().pipe(
      filter(event => event.key === 'Escape')
    ).subscribe(() => this.close());
  }

  protected close() {
    this.dialogRef.close(this.ticket ? {ticket: this.ticket, saved: false} : undefined);
  }

  protected get sortedComments(): CommentDetailsDto[] {
    return [...(this.ticket?.comments ?? [])].sort((a, b) => b.createdAt.getTime() - a.createdAt.getTime());
  }

  protected get isClosed(): boolean {
    return this.ticket?.status === TicketStatus.CLOSED;
  }

  /** Closed tickets are read-only for everyone except admins, who can still edit them
   * (in particular the status) in order to reopen one that was closed by mistake. */
  protected get isReadOnly(): boolean {
    return this.isClosed && !this.isAdmin;
  }

  protected get isFormValid(): boolean {
    const formValid = this.ticketFormComponent?.ticketForm?.valid ?? false;
    if (this.isNew) {
      return formValid && this.ticketFormComponent?.getSelectedScannableId() !== null;
    }

    return formValid;
  }

  protected save() {
    if (!this.isFormValid) {
      this.ticketFormComponent?.markAllFieldsAsTouched();

      this.snackBar.open('Tölts ki minden kötelező mezőt!', "Let's do it!", {
        duration: 3000,
        horizontalPosition: 'right',
        verticalPosition: 'top',
        panelClass: ['error-snackbar'],
      });
      return;
    }

    const formValue = this.ticketFormComponent.ticketForm.value;

    if (this.isNew) {
      const scannableId = this.ticketFormComponent.getSelectedScannableId()!;
      const newTicket = new TicketCreateDto(formValue.description, formValue.severity, scannableId);

      this.ticketService.createTicket(newTicket).subscribe(createdTicket => {
        this.dialogRef.close({ticket: createdTicket, saved: true});
      });
    } else {
      const updatedTicket = new TicketUpdateDto(formValue.description, formValue.status, formValue.severity);

      this.ticketService.updateTicket(this.ticket!.id, updatedTicket).subscribe(result => {
        this.dialogRef.close({ticket: result, saved: true});
      });
    }
  }

  protected sendComment() {
    const body = this.newCommentControl.value;
    if (!body || !this.ticket) {
      return;
    }

    this.ticketService.addComment(this.ticket.id, new CommentCreateDto(body)).subscribe(comment => {
      this.ticket!.comments = [...this.ticket!.comments, comment];
      this.newCommentControl.reset('');
      this.cdr.markForCheck();

      const closing = this.closingComment;
      this.closingComment = false;

      if (closing) {
        this.updateStatus(TicketStatus.CLOSED);
      } else if (this.ticket!.status === TicketStatus.OPEN) {
        this.updateStatus(TicketStatus.IN_PROGRESS);
      }
    });
  }

  private updateStatus(status: TicketStatus) {
    const ticket = this.ticket!;
    const updatedTicket = new TicketUpdateDto(ticket.description, status, ticket.severity);

    this.ticketService.updateTicket(ticket.id, updatedTicket).subscribe(result => {
      this.ticket = result;
      this.cdr.markForCheck();
    });
  }

  protected onDeviceNotFound() {
    this.snackBar.open('Nem található eszköz ezzel a vonalkóddal!', 'Értem', {
      duration: 3000,
      horizontalPosition: 'right',
      verticalPosition: 'top',
      panelClass: ['error-snackbar'],
    });
  }
}
