export class ClientRecord {
  public id: number;
  public name: string;
  public surname: string;
  public patronymic: string;
  public age: number;
  public middle_balance: number | null;
  public max_balance: number | null;
  public min_balance: number | null;

  public assign(o: any): ClientRecord {
    this.id = o.id;
    this.name = o.name;
    this.surname = o.surname;
    this.patronymic = o.patronymic;
    this.age = o.age;
    this.middle_balance = o.middle_balance;
    this.max_balance = o.max_balance;
    this.min_balance = o.min_balance;
    return this;
  }

  public static copy(a: any): ClientRecord {
    let ret = new ClientRecord();
    ret.assign(a);
    return ret;
  }
}