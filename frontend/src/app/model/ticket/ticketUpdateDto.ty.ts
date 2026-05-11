import {TicketSeverity} from './ticketSeverity';
import {TicketStatus} from './ticketStatus';

export class TicketUpdateDto {
  description: string;
  status: TicketStatus;
  severity: TicketSeverity;

  constructor(
    description: string,
    status: TicketStatus,
    severity: TicketSeverity
  ) {
    this.description = description;
    this.status = status;
    this.severity = severity;
  }

  toJson(): Record<string, unknown> {
    return {
      description: this.description,
      status: this.status,
      severity: this.severity
    };
  }
}
