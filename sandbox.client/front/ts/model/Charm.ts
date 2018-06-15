export class Charm {
  public id: number;
  public name: string;
  public description: string;
  public energy: number;

  public assign(o: any): Charm {
    this.id = o.id;
    this.name = o.name;
    this.description = o.description;
    this.energy = o.energy;
    return this;
  }

  public static copy(a: any): Charm {
    let ret = new Charm();
    ret.assign(a);
    return ret;
  }
}