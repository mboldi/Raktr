
export class LocationCreateDto {
  name: string;

  constructor(name: string) {
    this.name = name;
  }

  static fromJson(json: Record<string, unknown>): LocationCreateDto {
    return new LocationCreateDto(
      json['name'] as string);
  }
}
