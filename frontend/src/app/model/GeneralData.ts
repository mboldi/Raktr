export class GeneralData {
    public key: string;
    public data: string;

    constructor(key: string, data: string) {
        this.key = key;
        this.data = data;
    }

    static toJsonString(generalData: GeneralData): string {
        return `{\"GeneralData\": ${JSON.stringify(generalData)}}`;
    }
}
