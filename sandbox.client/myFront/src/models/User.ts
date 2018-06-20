import { Phone } from './Phone';
import { Address } from './Address';
import {CharmType} from './CharmType'
import {GenderType} from "./GenderType";




export class User {
    public id: string;
    public surname: string ;
    public name: string;
    public patronymic: string | null;
    public birthDate: number;
    public charm: CharmType | null;
    public phones: Phone[] ;
    public factualAddress: Address | null;
    public registeredAddress: Address;
    public genderType: GenderType;

    public assign(o: any): User{
        this.id = o.id;
        this.surname = o.surname;
        this.name = o.name;
        this.patronymic = o.patronymic;
        this.charm = o.charm;
        this.birthDate = o.birthDate;
        this.genderType = o.genderType;
        this.phones=o.phones;
        this.factualAddress = o.factualAddress;
        this.registeredAddress = o.registeredAddress;
        return this;
    }

    public static copy(a: any): User{
        let ret = new User();
        ret.assign(a);
        return ret;
    }
}
