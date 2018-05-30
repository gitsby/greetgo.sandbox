import {Character} from "./Character";
import {Address} from "./Address";
import {Phone} from "./Phone";

export class Client {
    public id:number;
    public name: string = "";
    public surname: string = "";
    public patronymic: string = "";
    public gender: string = "";
    public birthDate: string = "";


    public snmn: string = "";
    public age: number = 0;
    public accBalance: number = 10;
    public maxBalance: number = 10;
    public minBalance: number = 10;

    public charm: Character = null;

    // Addresses
    public addresses: Address[] = null;

    // Phone numbers
    public phones: Phone[] = null;
}