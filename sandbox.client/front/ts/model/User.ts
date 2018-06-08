import { Phone } from './Phone';
import { Address } from './Address';
import {CharmType} from './CharmType'



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

    public assign(o: any): User{
        this.id = o.id;
        this.surname = o.surname;
        this.name = o.name;
        this.patronymic = o.patronymic;
        this.phones = o.phones;
        return this;
    }

    public static copy(a: any): User{
        let ret = new User();
        ret.assign(a);
        return ret;
    }
}
