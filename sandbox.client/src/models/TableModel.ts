import {CharmType} from "./CharmType";

export class TableModel{
    public id:string;
    public fullName:string;
    public charm:CharmType|null;
    public age:number;
    public totalBalance:number;
    public maxBalance:number;
    public minBalance:number;

    public assign(o: any): TableModel{
      this.id = o.id;
      this.fullName = o.fullName;
      this.charm = o.charm;
      this.age = o.age;
      this.totalBalance = o.totalBalance;
      this.maxBalance = o.maxBalance;
      this.minBalance = o.minBalance;
      return this;
    }

    public static copy(a: any): TableModel{
      let ret = new TableModel();
      ret.assign(a);
      return ret;
    }
}
