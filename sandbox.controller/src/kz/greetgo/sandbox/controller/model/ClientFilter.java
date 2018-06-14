package kz.greetgo.sandbox.controller.model;

public class ClientFilter {
  public Integer from;
  public Integer to;
  public SortByEnum sortByEnum;
  public SortDirection sortDirection;
  public String fio;

  public ClientFilter() {}

  public ClientFilter(Integer from, Integer to, SortByEnum sortByEnum, SortDirection sortDirection, String fio) {
    this.from = from;
    this.to = to;
    this.sortByEnum = sortByEnum;
    this.sortDirection = sortDirection;
    this.fio = fio;
  }
}