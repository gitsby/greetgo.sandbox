import {Address} from "./Address";
import {Phone} from "./Phone";

export class ClientDetails {

    public id: number;
    public name: string = "";
    public surname: string = "";
    public patronymic: string = "";
    public gender: string = "";
    public birthDate: Date;


    public snmn: string = "";
    public age: number = 0;
    public accBalance: number = 10;
    public maxBalance: number = 10;
    public minBalance: number = 10;

    public charm: number = null;

    // Addresses
    public addresses: Address[] = null;

    // Phone numbers
    public phones: Phone[] = null;

    public toString = (): string => {
        return JSON.stringify(this);
    }
}