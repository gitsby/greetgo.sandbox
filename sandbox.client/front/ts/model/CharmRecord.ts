export class CharmRecord {
  public id: number;
  public name: string;
  public description: string;
  public energy: number;

  public assign(o: any): CharmRecord {
    this.id = o.id;
    this.name = o.name;
    this.description = o.description;
    this.energy = o.energy;
    return this;
  }

  public static copy(a: any): CharmRecord {
    let ret = new CharmRecord();
    ret.assign(a);
    return ret;
  }
}