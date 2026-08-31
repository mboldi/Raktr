import {ContainerAddItemDto} from './containerAddItemDto';

export class ContainerAddDevicesDto {
  items: ContainerAddItemDto[];

  constructor(items: ContainerAddItemDto[]) {
    this.items = items;
  }

  toJson(): Record<string, unknown> {
    return {
      items: this.items.map(item => item.toJson())
    };
  }
}
