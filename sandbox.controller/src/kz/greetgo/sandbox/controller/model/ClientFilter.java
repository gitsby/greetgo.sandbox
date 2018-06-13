package kz.greetgo.sandbox.controller.model;

public class ClientFilter {
  public Integer from;
  public Integer to;
  public SortBy sortBy;
  public SortDirection sortDirection;
  public String fio;

  public ClientFilter() {}

  public ClientFilter(Integer from, Integer to, SortBy sortBy, SortDirection sortDirection, String fio) {
    this.from = from;
    this.to = to;
    this.sortBy = sortBy;
    this.sortDirection = sortDirection;
    this.fio = fio;
  }
}