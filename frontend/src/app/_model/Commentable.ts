import {User} from './User';

export abstract class Commentable {
    type_: string
    id: number
    body: string
    dateOfWriting: Date
    writer: User

    constructor(type_: string, id: number, body: string, dateOfWriting: Date, writer: User) {
        this.type_ = type_;
        this.id = id;
        this.body = body;
        this.dateOfWriting = dateOfWriting;
        this.writer = writer;
    }

    abstract toJson(): String
}
