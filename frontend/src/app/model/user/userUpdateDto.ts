export class UserUpdateDto {
  nickname: string | null;
  personalId: string | null;

  constructor(nickName: string | null, personalId: string | null) {
    this.nickname = nickName;
    this.personalId = personalId;
  }

  static fromJson(json: Record<string, unknown>): UserUpdateDto {
    return new UserUpdateDto(json['userName'] as string, json['personalId'] as string);
  }
}
