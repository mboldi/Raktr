export class UserDetails {
  uuid: string;
  username: string;
  familyName: string;
  givenName: string;
  nickname: string | null;
  personalId: string | null;
  groups: string[];

  constructor(
    uuid: string,
    userName: string,
    familyName: string,
    givenName: string,
    nickName: string | null,
    personalId: string | null,
    groups: string[],
  ) {
    this.uuid = uuid;
    this.familyName = familyName;
    this.givenName = givenName;
    this.nickname = nickName;
    this.username = userName;
    this.personalId = personalId;
    this.groups = groups;
  }

  get displayName(): string {
    return this.nickname || `${this.familyName} ${this.givenName}`;
  }

  static fromJson(json: Record<string, unknown>): UserDetails {
    return new UserDetails(
      json['uuid'] as string,
      json['username'] as string,
      json['familyName'] as string,
      json['givenName'] as string,
      json['nickname'] as string | null,
      json['personalId'] as string | null,
      Array.isArray(json['groups']) ? (json['groups'] as string[]) : [],
    );
  }
}
