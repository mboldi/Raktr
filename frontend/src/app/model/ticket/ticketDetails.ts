import {TicketSeverity} from './ticketSeverity';
import {TicketStatus} from './ticketStatus';
import {Scannable} from '../scannable/scannable';
import {CommentDetailsDto} from '../comment/commentDetailsDto';
import {UserDetails} from '../user/userDetails';

export class TicketDetails {
  id: number;
  description: string;
  status: TicketStatus;
  severity: TicketSeverity;
  scannable: Scannable;
  comments: CommentDetailsDto[];
  createdAt: Date;
  createdBy: UserDetails;
  updatedAt: Date;
  updatedBy: UserDetails;

  constructor(
    id: number,
    description: string,
    status: TicketStatus,
    severity: TicketSeverity,
    scannable: Scannable,
    comments: CommentDetailsDto[],
    createdAt: Date,
    createdBy: UserDetails,
    updatedAt: Date,
    updatedBy: UserDetails
  ) {
    this.id = id;
    this.description = description;
    this.status = status;
    this.severity = severity;
    this.scannable = scannable;
    this.comments = comments;
    this.createdAt = createdAt;
    this.createdBy = createdBy;
    this.updatedAt = updatedAt;
    this.updatedBy = updatedBy;
  }

  static fromJson(json: Record<string, unknown>): TicketDetails {
    return new TicketDetails(
      json['id'] as number,
      json['description'] as string,
      json['status'] as TicketStatus,
      json['severity'] as TicketSeverity,
      Scannable.fromJson(json['scannable'] as Record<string, unknown>),
      (json['comments'] as Record<string, unknown>[]).map(CommentDetailsDto.fromJson),
      new Date(json['createdAt'] as string),
      UserDetails.fromJson(json['createdBy'] as Record<string, unknown>),
      new Date(json['updatedAt'] as string),
      UserDetails.fromJson(json['updatedBy'] as Record<string, unknown>)
    );
  }
}
