import {Gender} from "./Gender";
import {ClientAddress} from "./ClientAddress";
import {ClientPhone} from "./ClientPhone";
import {AddressTypeEnum} from "./AddressTypeEnum";
import {PhoneType} from "./PhoneType";

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

  constructor() {
    this.addressFact.type = AddressTypeEnum.FACT;
    this.addressReg.type = AddressTypeEnum.REG;
    this.homePhone.type = PhoneType.HOME;
    this.mobilePhone.type = PhoneType.MOBILE;
    this.workPhone.type = PhoneType.WORK;
  }
}