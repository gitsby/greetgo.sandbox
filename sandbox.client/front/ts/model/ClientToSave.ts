import {Gender} from "./Gender";
import {ClientAddress} from "./ClientAddress";
import {ClientPhone} from "./ClientPhone";

export class ClientToSave {
  public id: number;
  public surname: string;
  public name: string;
  public patronymic: string;
  public gender: Gender;
  public birthDate: Date;
  public charmId: number;
  public addressFact: ClientAddress = new ClientAddress();
  public addressReg: ClientAddress = new ClientAddress();
  public homePhone: ClientPhone = new ClientPhone();
  public workPhone: ClientPhone = new ClientPhone();
  public mobilePhone: ClientPhone = new ClientPhone();

  public assign(o: any): ClientToSave {
    this.id = o.id;
    this.surname = o.surname;
    this.name = o.name;
    this.patronymic = o.patronymic;
    this.gender = o.gender;
    this.birthDate = new Date(o.birthDate);
    this.charmId = o.charm.id;
    this.addressFact = o.addressFact;
    this.addressReg = o.addressReg;
    this.homePhone = o.homePhone;
    this.workPhone = o.workPhone;
    this.mobilePhone = o.mobilePhone;
    return this;
  }

  public static copy(a: any): ClientToSave {
    let ret = new ClientToSave();
    ret.assign(a);
    return ret;
  }
}