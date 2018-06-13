package kz.greetgo.sandbox.controller.model;

// FIXME: 6/13/18 charmDot
public class Charm {
  public int id;
  public String name;
  public String description;
  public float energy;

  public Charm() {}

  public Charm(int id, String name, String description, float energy) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.energy = energy;
  }
}
