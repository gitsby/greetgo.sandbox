import { PhoneType } from './PhoneType'
export class Phone{
    public number: string;
    public phoneType: PhoneType;
    constructor(number: string, phoneType: PhoneType) {
        this.number = number;
        this.phoneType = phoneType;
    }

    static copy(phone: Phone) {
        return new Phone(phone.number, phone.phoneType);
    }
}