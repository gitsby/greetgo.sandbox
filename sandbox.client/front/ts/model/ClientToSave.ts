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
}