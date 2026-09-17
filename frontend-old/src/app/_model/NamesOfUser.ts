
export class NamesOfUser {
    username: string;
    nickName: string;
    familyName: string;
    givenName: string;


    constructor(username: string = '', nickName: string = '', familyName: string = '', givenName: string = '') {
        this.username = username;
        this.nickName = nickName;
        this.familyName = familyName;
        this.givenName = givenName;
    }
}

