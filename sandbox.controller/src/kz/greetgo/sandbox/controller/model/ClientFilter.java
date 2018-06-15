package kz.greetgo.sandbox.controller.model;

public class ClientFilter {
  public Integer offset;
  public Integer limit;
  public SortByEnum sortByEnum;
  public SortDirection sortDirection;
  public String fio;

  public ClientFilter() {}

  public ClientFilter(Integer offset, Integer limit, SortByEnum sortByEnum, SortDirection sortDirection, String fio) {
    this.offset = offset;
    this.limit = limit;
    this.sortByEnum = sortByEnum;
    this.sortDirection = sortDirection;
    this.fio = fio;
  }
}