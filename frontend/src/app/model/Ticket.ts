import {Commentable} from './Commentable';
import {ProblemSeverity} from './ProblemSeverity';
import {Scannable} from './Scannable';
import {User} from './User';

export class Ticket extends Commentable {
    status: ProblemSeverity;
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

    constructor(id: number, body: string, dateOfWriting: Date, writer: User, status: ProblemSeverity,
                scannableOfProblem: Scannable, severity: ProblemSeverity) {
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
        return JSON.stringify(ticketJson);
    }
}
