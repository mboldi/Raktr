export class UserUpdateDto {
  nickName: string;
  personalId: string;

  constructor(nickName: string, personalId: string) {
    this.nickName = nickName;
    this.personalId = personalId;
  }

  static fromJson(json: Record<string, unknown>): UserUpdateDto {
    return new UserUpdateDto(
      json['userName'] as string,
      json['personalId'] as string
    );
  }
}
