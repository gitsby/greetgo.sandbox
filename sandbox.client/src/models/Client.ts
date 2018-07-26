import { Phone } from './Phone';
import { Address } from './Address';
import {GenderType} from "./GenderType";




export class Client {
    public id: number;
    public surname: string ;
    public name: string;
    public patronymic: string | null;
    public birthDate: number;
    public charmId: number | null;
    public phones: Phone[] ;
    public factualAddress: Address | null;
    public registeredAddress: Address;
    public genderType: GenderType;

    public assign(o: any): Client{
        this.id = o.id;
        this.surname = o.surname;
        this.name = o.name;
        this.patronymic = o.patronymic;
        this.charmId = o.charmId;
        this.birthDate = o.birthDate;
        this.genderType = o.genderType;
        this.phones=o.phones;
        this.factualAddress = o.factualAddress;
        this.registeredAddress = o.registeredAddress;
        return this;
    }

    public static copy(a: any): Client{
        let ret = new Client();
        ret.assign(a);
        return ret;
    }
}
