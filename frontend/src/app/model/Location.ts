export class Location {
    id: number;
    name: string;

    static fromJson(location: Location) {
        return new Location(
            location.id,
            location.name
        )
    }

    constructor(id: number, name: string) {
        this.id = id;
        this.name = name;
    }
}
