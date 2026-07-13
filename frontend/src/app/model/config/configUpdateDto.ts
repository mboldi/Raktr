export class ConfigUpdateDto {
  value: string;

  constructor(value: string) {
    this.value = value;
  }

  static fromJson(json: Record<string, unknown>): ConfigUpdateDto {
    return new ConfigUpdateDto(
      json['value'] as string
    );
  }

  toJson(): Record<string, unknown> {
    return {
      value: this.value
    };
  }
}
