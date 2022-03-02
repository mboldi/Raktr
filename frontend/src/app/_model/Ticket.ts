import {Commentable} from './Commentable';
import {ProblemSeverity} from './ProblemSeverity';
import {Scannable} from './Scannable';
import {User} from './User';
import {TicketStatus} from './TicketStatus';
import {Device} from './Device';
import {CompositeItem} from './CompositeItem';
import {Comment} from './Comment';

export class Ticket extends Commentable {
    status: TicketStatus;
    scannableOfProblem: Scannable;
    severity: ProblemSeverity;
    comments: Comment[];

    static fromJson(ticket: Ticket): Ticket {
        const newTicket = new Ticket(
            ticket.id,
            ticket.body,
            ticket.dateOfWriting,
            ticket.writer,
            ticket.status,
            null,
            ticket.severity
        );

        if (ticket.scannableOfProblem !== undefined) {
            if (ticket.scannableOfProblem['@type'] === 'device') {
                newTicket.scannableOfProblem = Device.fromJson(ticket.scannableOfProblem as Device);
            } else if (ticket.scannableOfProblem['@type'] === 'compositeItem') {
                newTicket.scannableOfProblem = CompositeItem.fromJson(ticket.scannableOfProblem as CompositeItem);
            }
        }

        // tslint:disable-next-line:forin
        for (const rawComment in ticket.comments) {
            newTicket.comments.push(Comment.fromJson(rawComment as unknown as Comment));
        }

        return newTicket;
    }

    constructor(id: number = -1, body: string = '', dateOfWriting: Date = new Date(), writer: User = null, status: TicketStatus = TicketStatus.OPEN,
                scannableOfProblem: Scannable = null, severity: ProblemSeverity = ProblemSeverity.SEVERE, comments: Comment[] = []) {
        super('ticket', id, body, dateOfWriting, writer);
        this.status = status;
        this.scannableOfProblem = scannableOfProblem;
        this.severity = severity;
        this.comments = comments;
    }

    toJson(): string {
        return `{\"Ticket\": ${this.toJsonWithoutRoot()}}`;
    }

    toJsonWithoutRoot(): string {
        const ticketJson = JSON.parse(JSON.stringify(this));
        ticketJson['@type'] = 'ticket';
        ticketJson['scannableOfProblem'] = JSON.parse(this.scannableOfProblem.toJsonWithoutRoot());
        return JSON.stringify(ticketJson);
    }
}
