import {AfterViewInit, Component} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientRecord} from "../../model/ClientRecord";
import {ClientFilter} from "../../model/ClientFilter";
import {SortDirection} from "../../model/SortDirection";
import {SortByEnum} from "../../model/SortByEnum";
import {ClientToSave} from "../../model/ClientToSave";

@Component({
  selector: 'clients-list-form-component',
  template: require('./clients_list_form.component.html'),
  styles: [require('./clients_list_form.component.css')],
})

export class ClientsListFormComponent implements AfterViewInit {
  searchInputText: string = "";
  editClientId: number | null = null;

  clientInfoFormComponentEnable: boolean = false;

  clientRecordsCount: number = 1;
  clientRecords: ClientRecord[] = [];
  clientFilter: ClientFilter = new ClientFilter();
  currentPage: number = 1;

  sortIndex: number = -1;
  numberOfItemInPage: number = 10;

  constructor(private httpService: HttpService) {}

  ngAfterViewInit(): void {
    this.loadRecordsCount();
  }

  loadClientRecordsList(page: number) {
    if (page <= 0) return;
    this.currentPage = page;
    this.clientFilter.offset = this.currentPage * this.numberOfItemInPage - this.numberOfItemInPage;
    this.clientFilter.limit = this.currentPage * this.numberOfItemInPage;
    this.loadPage();
  }

  pageChanged(page: number) {
    if (this.currentPage == page) return;
    this.loadClientRecordsList(page);
  }

  loadPage() {
    this.httpService.get("/client/list", {
      "clientFilter": JSON.stringify(this.clientFilter)
    }).toPromise().then(result => {
      this.clientRecords = new Array();
      for (let res of result.json()) {
        this.clientRecords.push(ClientRecord.copy(res));
      }
    })
  }

  loadRecordsCount() {
    this.clientRecordsCount = 1;
    this.httpService.get("/client/count", {
      "clientFilter": JSON.stringify(this.clientFilter)
    }).toPromise().then(result => {
      this.clientRecordsCount = result.json();
      this.loadClientRecordsList(this.currentPage);
    })
  }

  filterPage() {
    if (this.searchInputText == "") this.clientFilter.fio = null;
    else this.clientFilter.fio = this.searchInputText;
    this.currentPage = 1;
    this.loadRecordsCount();
  }

  clearFilter() {
    this.searchInputText = "";
    this.clientFilter.fio = null;
    this.currentPage = 1;
    this.loadRecordsCount();
  }

  sortPage(index: number) {
    this.currentPage = 1;
    if (this.sortIndex == index) {
      this.sortIndex = index + 100;
      this.clientFilter.sortDirection = SortDirection.DESCENDING;
      this.loadPage();
      return;
    }
    else if (this.sortIndex == index + 100) {
      this.sortIndex = -1;
      this.clientFilter.sortByEnum = SortByEnum.NONE;
      this.clientFilter.sortDirection = SortDirection.NONE;
      this.loadPage();
      return;
    }

    let sortBy: SortByEnum;
    switch (index) {
      case 0:
        sortBy = SortByEnum.FULL_NAME;
        break;
      case 2:
        sortBy = SortByEnum.AGE;
        break;
      case 3:
        sortBy = SortByEnum.MIDDLE_BALANCE;
        break;
      case 4:
        sortBy = SortByEnum.MAX_BALANCE;
        break;
      case 5:
        sortBy = SortByEnum.MIN_BALANCE;
        break;
    }

    this.sortIndex = index;
    this.clientFilter.sortDirection = SortDirection.ASCENDING;
    this.clientFilter.sortByEnum = sortBy;
    this.loadPage();
  }

  pagesCount(): number {
    let pagesCount = Math.ceil(this.clientRecordsCount / this.numberOfItemInPage);
    if (this.currentPage > pagesCount) {
      this.currentPage = pagesCount;
      this.loadClientRecordsList(this.currentPage);
    }
    return pagesCount;
  }

  range(from: number, to: number, step: number) {
    let rangeList: number[] = new Array<number>();
    let i = from;
    while (i < to) {
      rangeList.push(i);
      i += step;
    }
    return rangeList;
  }

  deleteClientButtonClicked(clientRecords: ClientRecord) {
    let ID = clientRecords.id;
    this.httpService.delete("/client/delete", {
      "clientId": ID
    }).toPromise().then(() => {
      this.loadPage();
      this.loadRecordsCount();
    });
  }

  editClientButtonClicked(clientId: number | null) {
    this.editClientId = clientId;
    this.clientInfoFormComponentEnable = true;
  }

  isLastPage(): boolean {
    return this.currentPage == this.pagesCount();
  }

  close(clientToSave: ClientToSave | null) {
    this.clientInfoFormComponentEnable = false;
    if (clientToSave == null) return;
    if (this.editClientId == null) this.insertRecord(clientToSave);
    else this.updateClient(clientToSave);
    this.editClientId = null;
  }

  private insertRecord(clientToSave: ClientToSave) {
    if (this.isLastPage() && this.clientRecords.length < this.numberOfItemInPage) {
      let clientRecord = new ClientRecord();
      clientRecord.id = clientToSave.id;
      clientRecord.surname = clientToSave.surname;
      clientRecord.name = clientToSave.name;
      clientRecord.patronymic = clientToSave.patronymic;
      clientRecord.age = 0;
      clientRecord.middle_balance = 0;
      clientRecord.max_balance = 0;
      clientRecord.min_balance = 0;
      this.clientRecords.push(clientRecord);
    }
    this.clientRecordsCount++;
  }

  private updateClient(clientToSave: ClientToSave) {
    for (let record of this.clientRecords)
      if (record.id == clientToSave.id) {
        record.surname = clientToSave.surname;
        record.name = clientToSave.name;
        record.patronymic = clientToSave.patronymic;
        break;
      }
  }
}