import {AfterViewInit, Component, Input} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientRecord} from "../../model/ClientRecord";
import {ClientFilter} from "../../model/ClientFilter";
import {SortDirection} from "../../model/SortDirection";
import {SortByEnum} from "../../model/SortByEnum";
import {FileTypeEnum} from "../../model/FileTypeEnum";

@Component({
  selector: 'clients-list-form-component',
  template: require('./clients_list_form.component.html'),
  styles: [require('./clients_list_form.component.css')],
})

export class ClientsListFormComponent implements AfterViewInit {
  @Input() userName: string;

  searchInputText: string = "";
  editClientId: number | null = null;

  clientInfoFormComponentEnable: boolean = false;

  clientRecordsCount: number = 1;
  clientRecords: ClientRecord[] = [];
  clientFilter: ClientFilter = new ClientFilter();
  currentPage: number = 1;

  sortIndex: number = -1;
  numberOfItemInPage: number = 10;

  constructor(private httpService: HttpService) {

  }

  ngAfterViewInit(): void {
    this.loadRecordsCount();
  }

  loadClientRecordsList(page: number) {
    if (page <= 0) return;
    this.currentPage = page;
    this.clientFilter.offset = this.currentPage * this.numberOfItemInPage - this.numberOfItemInPage;
    this.clientFilter.limit = this.numberOfItemInPage;
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
      this.clientRecords = [];
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

  close(clientRecord: ClientRecord | null) {
    this.clientInfoFormComponentEnable = false;
    this.editClientId = null;
    if (clientRecord != null) this.insertRecord(clientRecord);
  }

  private insertRecord(clientRecord: ClientRecord) {
    for (let i = 0; i < this.clientRecords.length; i++) {
      if (this.clientRecords[i].id == clientRecord.id) {
        this.clientRecords[i] = clientRecord;
        this.clientRecordsCount++;
        return;
      }
    }
    this.clientRecords.push(clientRecord);
    this.clientRecordsCount++;
  }

  getRenderClicked(i: number) {
    switch (i) {
      case 0:
        this.getRender(FileTypeEnum.XLSX);
        break;
      case 1:
        this.getRender(FileTypeEnum.PDF);
        break;
    }
  }

  getRender(fileType: FileTypeEnum) {
    let url = "/report/get-render?fileName="+this.userName+"&clientFilter="+JSON.stringify(this.clientFilter)+"&fileTypeEnum="+JSON.stringify(fileType);
    window.open(this.httpService.url(url));
  }

  migrate() {
    alert("migration");
    this.httpService.get("/migration/start").toPromise().then();
  }
}