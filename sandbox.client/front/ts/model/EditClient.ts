import {Character} from "./Character";
import {Address} from "./Address";
import {Phone} from "./Phone";

export class EditClient {
    public id: number;
    public name: string;
    public surname: string;
    public patronymic: string;
    public gender: string;
    public birthDate: string;

    public charm: number;

    public addedAddresses: Address[] = [];
    public editedAddresses: Address[] = [];
    public deletedAddresses: Address[] = [];

    public addedPhones: Phone[] = [];
    public deletedPhones: Phone[] = [];
    public editedPhones: Phone[] = [];

    public toString = () : string=>{
        return JSON.stringify(this);
    }
}