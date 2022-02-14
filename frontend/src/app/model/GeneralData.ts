export class GeneralData {
    public key: string;
    public data: string;

    static toJsonString(generalData: GeneralData): string {
        return `{\"GeneralData\": ${JSON.stringify(generalData)}}`;
    }

    static fromJson(data: GeneralData): GeneralData {
        return new GeneralData(
            data.key,
            data.data
        )
    }

    constructor(key: string, data: string) {
        this.key = key;
        this.data = data;
    }
}
