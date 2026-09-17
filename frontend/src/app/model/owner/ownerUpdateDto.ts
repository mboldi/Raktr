export class OwnerUpdateDto {
  public name: string;
  public inSchInventory: boolean;

  constructor(name: string, inSchInventory: boolean) {
    this.name = name;
    this.inSchInventory = inSchInventory;
  }
}
