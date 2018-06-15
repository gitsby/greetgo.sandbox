import {Gender} from "./Gender";
import {ClientAddress} from "./ClientAddress";
import {ClientPhone} from "./ClientPhone";
import {Charm} from "./Charm";
import {ClientToSave} from "./ClientToSave";

export class Details {
  public id: number;
  public surname: string;
  public name: string;
  public patronymic: string;
  public gender: Gender;
  public birthDate: Date;
  public charm: Charm;
  public addressFact: ClientAddress;
  public addressReg: ClientAddress;
  public homePhone: ClientPhone;
  public workPhone: ClientPhone;
  public mobilePhone: ClientPhone;

  public assign(o: any): Details {
    this.id = o.id;
    this.surname = o.surname;
    this.name = o.name;
    this.patronymic = o.patronymic;
    this.gender = o.gender;
    this.birthDate = new Date(o.birthDate);
    this.charm = o.charm;
    this.addressFact = o.addressFact;
    this.addressReg = o.addressReg;
    this.homePhone = o.homePhone;
    this.workPhone = o.workPhone;
    this.mobilePhone = o.mobilePhone;
    return this;
  }

  public static copy(a: any): Details {
    let ret = new Details();
    ret.assign(a);
    return ret;
  }

  toClientToSave(): ClientToSave {
    let clientToSave = new ClientToSave();
    clientToSave.id = this.id;
    clientToSave.surname = this.surname;
    clientToSave.name = this.name;
    clientToSave.patronymic = this.patronymic;
    clientToSave.gender = this.gender;
    clientToSave.birthDate = this.birthDate;
    clientToSave.charmId = this.charm.id;
    clientToSave.addressFact = this.addressFact;
    clientToSave.addressReg = this.addressReg;
    clientToSave.homePhone = this.homePhone;
    clientToSave.workPhone = this.workPhone;
    clientToSave.mobilePhone = this.mobilePhone;
    return clientToSave;
  }
}