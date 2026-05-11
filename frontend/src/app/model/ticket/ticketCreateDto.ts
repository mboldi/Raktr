import {TicketSeverity} from './ticketSeverity';

export class TicketCreateDto {
  description: string;
  severity: TicketSeverity;
  scannableId: number;

  constructor(
    description: string,
    severity: TicketSeverity,
    scannableId: number
  ) {
    this.description = description;
    this.severity = severity;
    this.scannableId = scannableId;
  }

  toJson(): Record<string, unknown> {
    return {
      description: this.description,
      severity: this.severity,
      scannableId: this.scannableId
    };
  }
}
