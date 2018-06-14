package kz.greetgo.sandbox.controller.model;

public class Charm {
  public Integer id;
  public String name;
  public String description;
  public float energy;

  public Charm() {}

  public Charm(Integer id, String name, String description, float energy) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.energy = energy;
  }
}