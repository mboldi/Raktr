export class UserRole {
    id: number;
    roleName: string;

    static fromJson(userRole: UserRole) {
        return new UserRole(
            userRole.id,
            userRole.roleName
        )
    }

    constructor(id: number, roleName: string) {
        this.id = id;
        this.roleName = roleName;
    }
}
