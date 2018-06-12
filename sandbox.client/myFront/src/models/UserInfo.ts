import {PhoneType} from "./PhoneType";

export class UserInfo {
  public id: string;
  public accountName: string;
  public surname: string | null;
  public name: string | null;
  public patronymic: string | null;
  public yellow: boolean;

  public phoneType: PhoneType | null;

  public assign(o: any): UserInfo {
    this.id = o.id;
    this.accountName = o.accountName;
    this.surname = o.surname;
    this.name = o.name;
    this.patronymic = o.patronymic;
    this.phoneType = o.phoneType;
    this.yellow = o.yellow;
    return this;
  }

  public static copy(a: any): UserInfo {
    let ret = new UserInfo();
    ret.assign(a);
    return ret;
  }
}
