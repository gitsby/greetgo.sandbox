export class Phone {

    public client: number;
    public number: string = '';
    public type: string = '';

    public equals(phone: Phone): number {
        if (this.number == phone.number && this.type == phone.type &&
            this.client == phone.client) {
            return 0;
        }
        return -1;
    }
}
