import {Commentable} from './Commentable';
import {ProblemSeverity} from './ProblemSeverity';
import {Scannable} from './Scannable';
import {User} from './User';
import {TicketStatus} from './TicketStatus';

export class Ticket extends Commentable {
    status: TicketStatus;
    scannableOfProblem: Scannable;
    severity: ProblemSeverity;

    static fromJson(ticket: Ticket): Ticket {
        return new Ticket(
            ticket.id,
            ticket.body,
            ticket.dateOfWriting,
            ticket.writer,
            ticket.status,
            ticket.scannableOfProblem,
            ticket.severity
        );
    }

    constructor(id: number = -1, body: string = '', dateOfWriting: Date = new Date(), writer: User = null, status: TicketStatus = TicketStatus.OPEN,
                scannableOfProblem: Scannable = null, severity: ProblemSeverity = ProblemSeverity.SEVERE) {
        super('ticket', id, body, dateOfWriting, writer);
        this.status = status;
        this.scannableOfProblem = scannableOfProblem;
        this.severity = severity;
    }

    toJson(): String {
        return `{\"Ticket\": ${this.toJsonWithoutRoot()}}`;
    }

    toJsonWithoutRoot(): string {
        const ticketJson = JSON.parse(JSON.stringify(this));
        ticketJson['@type'] = 'ticket';
        ticketJson['scannableOfProblem'] = JSON.parse(this.scannableOfProblem.toJsonWithoutRoot());
        return JSON.stringify(ticketJson);
    }
}
