import {TicketStatus} from './ticketStatus';
import {TicketSeverity} from './ticketSeverity';

export const TICKET_STATUS_ORDER: Record<TicketStatus, number> = {
  [TicketStatus.OPEN]: 0,
  [TicketStatus.IN_PROGRESS]: 1,
  [TicketStatus.CLOSED]: 2,
};

export const TICKET_SEVERITY_ORDER: Record<TicketSeverity, number> = {
  [TicketSeverity.MINOR]: 0,
  [TicketSeverity.MAJOR]: 1,
  [TicketSeverity.CRITICAL]: 2,
};

export const TICKET_STATUS_LABELS: Record<TicketStatus, string> = {
  [TicketStatus.OPEN]: 'Nyitva',
  [TicketStatus.IN_PROGRESS]: 'Folyamatban',
  [TicketStatus.CLOSED]: 'Lezárva',
};

export const TICKET_SEVERITY_LABELS: Record<TicketSeverity, string> = {
  [TicketSeverity.MINOR]: 'Enyhe',
  [TicketSeverity.MAJOR]: 'Közepes',
  [TicketSeverity.CRITICAL]: 'Súlyos',
};
