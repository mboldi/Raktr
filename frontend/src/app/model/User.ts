import {UserRole} from './UserRole';

export class User {
    id: number;
    username: string;
    nickName: string;
    familyName: string;
    givenName: string;
    personalId: string;
    roles: UserRole[];
    token?: string;

    static isStudioMember(user: User): boolean {
        for (let i = 0; i < user.roles.length; i++) {
            const role = user.roles[i];
            if (role.roleName === 'ROLE_Stúdiós') {
                return true;
            }
        }

        return false;
    }

    constructor(id: number, username: string, nickName: string, familyName: string, givenName: string, personalId: string, roles: UserRole[], token?: string) {
        this.id = id;
        this.username = username;
        this.nickName = nickName;
        this.familyName = familyName;
        this.givenName = givenName;
        this.personalId = personalId;
        this.roles = roles;
        this.token = token;
    }
}
