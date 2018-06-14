import {Component, ViewChild} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientRecord} from "../../model/ClientRecord";
import {ClientEditFormComponent} from "../edit_form/client-edit-form.component";
import {ClientRecordFilter} from "../../model/ClientRecordFilter";

@Component({
  selector: 'list-form-component',
  template: require("./client-list.component.html"),
  styles: [require('./client-list.component.css')],
})
export class ClientListComponent {
  @ViewChild(ClientEditFormComponent) child;

  openEditingClient: boolean = false;

  paginationNum = 10;

  clients: ClientRecord[] = [];
  totalClients = 0;

  clientRecordFilter: ClientRecordFilter = new ClientRecordFilter();

  pagesList = [];


  constructor(private httpService: HttpService) {
    this.loadClientsNum();
    this.loadClients();
  }

  searchClicked() {
    if (this.clientRecordFilter.searchName.length > 2 || this.clientRecordFilter.searchName.length == 0) {
      this.clientRecordFilter.paginationPage = 0;
      this.loadClients();
      this.loadClientsNum();
    } else {
      alert("At least 3 symbols");
    }
  }

  addNewClientClicked() {
    this.openEditingClient = true;
  }

  editClick(index: any) {
    this.openEditingClient = true;
    this.child.loadFromDatabase(this.clients[index].id);
  }

  clearClicked() {
    this.clientRecordFilter.searchName = "";
    this.pageChanged(0);
  }

  loadClients() {
    this.httpService.get("/client/get-clients", {
      filter: JSON.stringify(this.clientRecordFilter)
    }).toPromise().then(result => {
      this.clients = [];
      for (let res of result.json()) {
        this.clients.push(res);
      }
    }, error => {
      alert("Error   " + error.toString())
    });
  }

  closeEditingForm() {
    this.openEditingClient = false;
  }

  loadClientsNum() {
    this.httpService.get("/client/get-client-count", {
      filter: JSON.stringify(this.clientRecordFilter)
    }).toPromise().then(result => {
      this.totalClients = result.json();
      this.calculatePagination();
    }, error => {
      alert(error)
    })
  }

  getPagesCount(num: number): number {
    return num / this.clientRecordFilter.sliceNum
      + ((num % this.clientRecordFilter.sliceNum == 0) ? 0 : 1)
  }

  calculatePagination() {
    this.paginationNum = Math.floor(this.getPagesCount(this.totalClients));
    console.log(this.getPagesCount(this.totalClients));

    this.pagesList = [];
    if ((this.clientRecordFilter.paginationPage == 0 || this.clientRecordFilter.paginationPage == 1) && this.paginationNum > 1) {
      let checkerNum = 3;
      if (this.paginationNum == 2) {
        checkerNum = 2;
      }
      this.addToPagArray(0, checkerNum);
      return;
    }

    if (this.clientRecordFilter.paginationPage > 1 && this.clientRecordFilter.paginationPage < this.paginationNum - 2) {
      this.addToPagArray(this.clientRecordFilter.paginationPage - 1, this.clientRecordFilter.paginationPage + 2);
      return;
    }

    if (this.clientRecordFilter.paginationPage >= this.paginationNum - 3 && this.paginationNum > 2) {
      this.addToPagArray(this.paginationNum - 3, this.paginationNum);
    }
  }

  addToPagArray(from: number, to: number) {
    for (let i = from; i < to; i++) {
      this.pagesList.push(i);
    }
  }

  pageSizeChanged() {
    this.pageChanged(0);
  }

  pageChanged(pageNum: number) {
    this.clientRecordFilter.paginationPage = pageNum;
    this.loadClients();
    this.calculatePagination();
  }

  sortBy(columnName: string) {
    if (this.clientRecordFilter.columnName == columnName) {
      this.clientRecordFilter.columnName = '-' + columnName;
    } else if (this.clientRecordFilter.columnName == '-' + columnName) {
      this.clientRecordFilter.columnName = 'empty';
    } else {
      this.clientRecordFilter.columnName = columnName;
    }
    this.loadClients();
  }

  deleteClient(deleteIndex: any) {
    this.httpService.delete("/client/delete", {
      index: this.clients[deleteIndex].id
    }).toPromise().then(result => {
      this.loadClients();
      this.loadClientsNum();
    }, error => {
      alert(error)
    });
  }

  increaseCurrentPagination() {
    this.clientRecordFilter.paginationPage++;
    if (this.clientRecordFilter.paginationPage > this.paginationNum - 1) {
      this.clientRecordFilter.paginationPage = 0;
    }
    this.loadClients();
    this.calculatePagination();
  }

  decreaseCurrentPagination() {
    this.clientRecordFilter.paginationPage--;
    if (this.clientRecordFilter.paginationPage < 0) {
      this.clientRecordFilter.paginationPage = this.paginationNum - 1;
    }
    this.loadClients();
    this.calculatePagination();
  }

  addNewClient(client: ClientRecord) {
    if (this.notExistedClient(client.id)) {
      this.clients.pop();
      this.clients.unshift(client)
    } else {
      this.clients.forEach((value, index) => {
        if (value.id == client.id)
          this.clients[index] = client;
      });
    }
  }

  notExistedClient(id: number): Boolean {
    for (let client of this.clients) {
      if (client.id == id) {
        return false;
      }
    }
    return true;
  }

}