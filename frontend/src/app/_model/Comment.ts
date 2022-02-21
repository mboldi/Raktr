import {User} from './User';
import {Commentable} from './Commentable';

export class Comment extends Commentable {

    static fromJson(comment: Comment): Comment {
        return new Comment(
            comment.id,
            comment.body,
            comment.dateOfWriting,
            comment.writer
        );
    }

    constructor(id: number, body: string, dateOfWriting: Date, writer: User) {
        super('comment',
            id,
            body,
            dateOfWriting,
            writer);
    }

    toJson(): String {
        return `{\"Comment\": ${this.toJsonWithoutRoot()}}`;
    }

    toJsonWithoutRoot(): string {
        const commentJson = JSON.parse(JSON.stringify(this));
        commentJson['@type'] = 'comment';
        return JSON.stringify(commentJson);
    }
}
