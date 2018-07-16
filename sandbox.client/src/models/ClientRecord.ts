
export class ClientRecord{
    public id:number;
    public fullName:string;
    public charmId:number;
    public age:number;
    public totalBalance:number;
    public maxBalance:number;
    public minBalance:number;

    public assign(o: any): ClientRecord{
      this.id = o.id;
      this.fullName = o.fullName;
      this.charmId = o.charmId;
      this.age = o.age;
      this.totalBalance = o.totalBalance;
      this.maxBalance = o.maxBalance;
      this.minBalance = o.minBalance;
      return this;
    }

    public static copy(a: any): ClientRecord{
      let ret = new ClientRecord();
      ret.assign(a);
      return ret;
    }
}
