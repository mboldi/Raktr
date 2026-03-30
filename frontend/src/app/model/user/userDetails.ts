export class UserDetails {
  uuid: string;
  userName: string;
  familyName: string;
  givenName: string;
  nickName: string;
  personalId: string;
  groups: string[];

  constructor(uuid: string, userName: string, familyName: string, givenName: string, nickName: string, personalId: string, groups: string[]) {
    this.uuid = uuid;
    this.familyName = familyName;
    this.givenName = givenName;
    this.nickName = nickName;
    this.userName = userName;
    this.personalId = personalId;
    this.groups = groups;
  }

  static fromJson(json: Record<string, unknown>): UserDetails {
    return new UserDetails(
      json['uuid'] as string,
      json['userName'] as string,
      json['familyName'] as string,
      json['givenName'] as string,
      json['nickName'] as string,
      json['personalId'] as string,
      Array.isArray(json['groups']) ? (json['groups'] as string[]) : []
    );
  }
}
