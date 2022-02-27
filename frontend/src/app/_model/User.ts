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

    static fromJson(user: User) {
        const userFromJson = new User(
            user.id,
            user.username,
            user.nickName,
            user.familyName,
            user.givenName,
            user.personalId
        );

        user.roles.forEach(role => userFromJson.roles.push(UserRole.fromJson(role)));

        return userFromJson;
    }

    isFullAccessMember(): boolean {
        return this.checkRole('ROLE_Stúdiós');
    }

    isAdmin(): boolean {
        return this.checkRole('ROLE_ADMIN');
    }

    checkRole(wantedRole: string): boolean {
        for (let i = 0; i < this.roles.length; i++) {
            const role = this.roles[i];

            if (role.roleName === wantedRole) {
                return true;
            }
        }

        return false;
    }

    constructor(id: number, username: string, nickName: string, familyName: string, givenName: string, personalId: string, roles: UserRole[] = [], token?: string) {
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
