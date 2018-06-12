import {Gender} from "./Gender";
import {ClientAddress} from "./ClientAddress";
import {ClientPhone} from "./ClientPhone";
import {Charm} from "./Charm";

export class ClientDetail {
  public id: number;
  public name: string;
  public surname: string;
  public patronymic: string;
  public gender: Gender;
  public birth_day: Date;
  public charm: Charm;
  public addressFact: ClientAddress;
  public addressReg: ClientAddress;
  public homePhone: ClientPhone;
  public workPhone: ClientPhone;
  public mobilePhone: ClientPhone;

  public assign(o: any): ClientDetail {
    this.id = o.id;
    this.name = o.name;
    this.surname = o.surname;
    this.patronymic = o.patronymic;
    this.gender = o.gender;
    this.birth_day = o.birth_day;
    this.charm = o.charm;
    this.addressFact = o.addressFact;
    this.addressReg = o.addressReg;
    this.homePhone = o.homePhone;
    this.workPhone = o.workPhone;
    this.mobilePhone = o.mobilePhone;
    return this;
  }

  public static copy(a: any): ClientDetail {
    let ret = new ClientDetail();
    ret.assign(a);
    return ret;
  }
}