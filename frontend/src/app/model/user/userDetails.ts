export class UserDetails {
  uuid: string;
  username: string;
  familyName: string;
  givenName: string;
  nickname: string;
  personalId: string;
  groups: string[];

  constructor(uuid: string, userName: string, familyName: string, givenName: string, nickName: string, personalId: string, groups: string[]) {
    this.uuid = uuid;
    this.familyName = familyName;
    this.givenName = givenName;
    this.nickname = nickName;
    this.username = userName;
    this.personalId = personalId;
    this.groups = groups;
  }

  static fromJson(json: Record<string, unknown>): UserDetails {
    return new UserDetails(
      json['uuid'] as string,
      json['username'] as string,
      json['familyName'] as string,
      json['givenName'] as string,
      json['nickname'] as string,
      json['personalId'] as string,
      Array.isArray(json['groups']) ? (json['groups'] as string[]) : []
    );
  }
}
