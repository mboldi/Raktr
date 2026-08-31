export class UserUpdateDto {
  nickname: string;
  personalId: string;

  constructor(nickName: string, personalId: string) {
    this.nickname = nickName;
    this.personalId = personalId;
  }

  static fromJson(json: Record<string, unknown>): UserUpdateDto {
    return new UserUpdateDto(
      json['userName'] as string,
      json['personalId'] as string
    );
  }
}
