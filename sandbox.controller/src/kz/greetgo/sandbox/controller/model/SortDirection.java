package kz.greetgo.sandbox.controller.model;

public enum SortDirection {
  NONE(""),
  ASCENDING("ASC"),
  DESCENDING("DESC"),
  ;
  private final String direction;
  SortDirection(String direction) {
    this.direction = direction;
  }

  @Override
  public String toString() {
    return direction;
  }
}
