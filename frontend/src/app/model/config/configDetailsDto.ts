export class ConfigDetailsDto {
  key: string;
  value: string;
  dataType: string;

  constructor(
    key: string,
    value: string,
    dataType: string
  ) {
    this.key = key;
    this.value = value;
    this.dataType = dataType;
  }

  static fromJson(json: Record<string, unknown>): ConfigDetailsDto {
    return new ConfigDetailsDto(
      json['key'] as string,
      json['value'] as string,
      json['dataType'] as string
    );
  }

  toJson(): Record<string, unknown> {
    return {
      key: this.key,
      value: this.value,
      dataType: this.dataType
    };
  }
}
