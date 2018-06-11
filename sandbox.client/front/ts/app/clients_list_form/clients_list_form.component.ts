import {Component, ElementRef, EventEmitter, Output, ViewChild} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientRecords} from "../../model/ClientRecords";
import {SortBy} from "../../model/SortBy";
import {ClientFilter} from "../../model/ClientFilter";
import {SortDirection} from "../../model/SortDirection";

@Component({
  selector: 'clients-list-form-component',
  template: require('./clients_list_form.component.html'),
  styles: [require('./clients_list_form.component.css')],
})
//fixme при переходе на последнюю страницу, появляется страница 0, которая не работает
export class ClientsListFormComponent {
  @Output() editClient = new EventEmitter<number>();
  @ViewChild('myModule') module: ElementRef;
  //FIXME use ngModel
  @ViewChild('searchInput') searchInput: ElementRef;
  //fixme удали если не используется
  @ViewChild('numberOfItemInPageSelector') numberOfItemInPageSelector: ElementRef;

  clientRecordsCount: number = 1;
  clientRecords: ClientRecords[] = [];
  clientFilter: ClientFilter = new ClientFilter();
  currentPage: number = 1;

  sortIndex: number = -1;
  numberOfItemInPage: number = 10;

  constructor(private httpService: HttpService) {
    this.loadRecordsCount();
  }

  loadClientRecordsList(page: number) {
    if (page <= 0) return;
    this.currentPage = page;
    this.clientFilter.from = this.currentPage * this.numberOfItemInPage - this.numberOfItemInPage;
    this.clientFilter.to = this.currentPage * this.numberOfItemInPage;
    this.loadPage();
  }

  pageChanged(page: number) {
    if (this.currentPage == page) return;
    this.loadClientRecordsList(page);
  }

  loadPage() {
    this.httpService.get("/client/records", {
      "clientFilter": JSON.stringify(this.clientFilter)
    }).toPromise().then(result => {
      this.clientRecords = new Array();
      for (let res of result.json()) {
        this.clientRecords.push(ClientRecords.copy(res));
      }
    })
  }

  loadRecordsCount() {
    this.clientRecordsCount = 1;
    this.httpService.get("/client/recordsCount", {
      "clientFilter": JSON.stringify(this.clientFilter)
    }).toPromise().then(result => {
      this.clientRecordsCount = result.json();
      this.loadClientRecordsList(this.currentPage);
    })
  }

  filterPage() {
    let search_text: string = this.searchInput.nativeElement.value;
    if (search_text == "") this.clientFilter.fio = null;
    else this.clientFilter.fio = search_text;
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
      this.clientFilter.sortBy = SortBy.NONE;
      this.clientFilter.sortDirection = SortDirection.NONE;
      this.loadPage();
      return;
    }

    let sortBy: SortBy;
    switch (index) {
      case 0:
        sortBy = SortBy.NAME;
        break;
      case 1:
        sortBy = SortBy.SURNAME;
        break;
      case 2:
        sortBy = SortBy.AGE;
        break;
      case 3:
        sortBy = SortBy.MIDDLE_BALANCE;
        break;
      case 4:
        sortBy = SortBy.MAX_BALANCE;
        break;
      case 5:
        sortBy = SortBy.MIN_BALANCE;
        break;
    }

    this.sortIndex = index;
    this.clientFilter.sortDirection = SortDirection.ASCENDING;
    this.clientFilter.sortBy = sortBy;
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

  deleteClientButtonClicked(clientRecords: ClientRecords) {
    let ID = clientRecords.id;
    this.httpService.delete("/client/remove", {
      "clientId": ID
    }).toPromise().then(() => {
      this.loadPage();
      this.loadRecordsCount();
    });
  }

  editClientButtonClicked(clientId: number) {
    this.editClient.emit(clientId);
  }
}