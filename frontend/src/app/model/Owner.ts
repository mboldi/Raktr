export class Owner {
    id: number;
    name: string;
    inSchInventory: boolean;

    static fromJson(owner: Owner) {
        return new Owner(
            owner.id,
            owner.name,
            owner.inSchInventory
        )
    }

    static toJsonString(owner: Owner): string {
        return `{\"Owner\": ${JSON.stringify(owner)}`;
    }

    constructor(id: number, name: string, inSchInventory: boolean = false) {
        this.id = id;
        this.name = name;
        this.inSchInventory = inSchInventory;
    }
}
