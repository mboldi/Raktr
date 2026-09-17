
export class CategoryCreateDto {
  name: string;

  constructor(name: string) {
    this.name = name;
  }

  static fromJson(json: Record<string, unknown>): CategoryCreateDto {
    return new CategoryCreateDto(
      json['name'] as string);
  }
}
