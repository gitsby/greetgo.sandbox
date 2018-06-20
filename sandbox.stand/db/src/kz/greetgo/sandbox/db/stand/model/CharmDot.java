package kz.greetgo.sandbox.db.stand.model;

import kz.greetgo.sandbox.controller.model.CharmRecord;

public class CharmDot {
  public Integer id;
  public String name;
  public String description;
  public float energy;

  public CharmRecord toCharm() {
    CharmRecord charmRecord = new CharmRecord();
    charmRecord.id = this.id;
    charmRecord.name = this.name;
    charmRecord.description = this.description;
    charmRecord.energy = this.energy;
    return charmRecord;
  }
}
