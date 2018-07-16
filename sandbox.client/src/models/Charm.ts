import {Client} from "./Client";

export class Charm{
  public id: number;
  public name: string;

  public assign(o: any): Charm {
    this.id = o.id;
    this.name = o.name;
    return this;
  }

  public static copy(a: any): Charm{
    let ret = new Client();
    ret.assign(a);
    return ret;
  }
}
