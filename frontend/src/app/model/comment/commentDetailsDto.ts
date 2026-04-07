import {UserDetails} from '../user/userDetails';

export class CommentDetailsDto {
  id: number;
  body: string;
  createdAt: Date;
  createdBy: UserDetails;
  updatedAt: Date;
  updatedBy: UserDetails;

  constructor(
    id: number,
    body: string,
    createdAt: Date,
    createdBy: UserDetails,
    updatedAt: Date,
    updatedBy: UserDetails
  ) {
    this.id = id;
    this.body = body;
    this.createdAt = createdAt;
    this.createdBy = createdBy;
    this.updatedAt = updatedAt;
    this.updatedBy = updatedBy;
  }

  static fromJson(json: Record<string, unknown>): CommentDetailsDto {
    return new CommentDetailsDto(
      json['id'] as number,
      json['body'] as string,
      new Date(json['createdAt'] as string),
      UserDetails.fromJson(json['createdBy'] as Record<string, unknown>),
      new Date(json['updatedAt'] as string),
      UserDetails.fromJson(json['updatedBy'] as Record<string, unknown>)
    );
  }
}
