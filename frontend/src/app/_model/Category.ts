export class Category {
    id: number;
    name: string;

    static fromJson(category: Category): Category {
        return new Category(
            category.id,
            category.name
        );
    }

    constructor(id: number, name: string) {
        this.id = id;
        this.name = name;
    }

    toJson(): String {
        return `{\"Category\": ${JSON.stringify(this)}}`;
    }
}
