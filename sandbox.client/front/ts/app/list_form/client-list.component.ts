import {Component, ViewChild} from "@angular/core";
import {HttpService} from "../HttpService";
import {RecordClient} from "../../model/RecordClient";
import {ClientEditFormComponent} from "../edit_form/client-edit-form.component";
import {ClientRecordPhilter} from "../../model/ClientRecordPhilter";

@Component({
  selector: 'list-form-component',
  template: require("./client-list.component.html"),
  styles: [require('./client-list.component.css')],
})
export class ClientListComponent {
  @ViewChild(ClientEditFormComponent) child;

  openEditingClient: boolean = false;

  paginationNum = 10;

  clients: RecordClient[] = [];
  totalClients = 0;
  //fixme filter
  clientRecordPhilter: ClientRecordPhilter = new ClientRecordPhilter();

  //fixme name primerno: pages list
  tempPaginationArray = [];


  constructor(private httpService: HttpService) {
    this.loadPaginationNum();
    this.loadClients();
  }

  searchClicked() {
    if (this.clientRecordPhilter.searchName.length > 2 || this.clientRecordPhilter.searchName.length == 0) {
      //fixme sdes' mojno delat' tol'ko dva deistvia: loadClientsList i loadClientsCount
      this.loadClients();
      this.loadClientSlice(0);
    } else {
      alert("At least 3 symbols");
    }
  }

  //fixme name: add new client clicked
  plusClick() {
    this.openEditingClient = true;
  }

  editClick(index: any) {
    this.openEditingClient = true;
    //fixme kak clientId mojet byt' null?
    if (this.clients[index].id != null) {
      this.child.loadFromDatabase(this.clients[index].id);
    }
  }

  loadClients() {
    this.httpService.get("/client/getClients", {
      philter: JSON.stringify(this.clientRecordPhilter)
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

  loadPaginationNum() {
    this.httpService.get("/client/getPaginationNum", {
      philter: JSON.stringify(this.clientRecordPhilter)
    }).toPromise().then(result => {
      this.totalClients = result.json();
      this.calculateChanges();
    }, error => {
      alert(error)
    })
  }

  //fixme calculate pagesCount
  calculateSliceNum(num: number): number {
    return num / this.clientRecordPhilter.sliceNum
      + ((num % this.clientRecordPhilter.sliceNum == 0) ? 0 : 1)
  }

  //fixme calculate pagination
  calculateChanges() {
    //fixme v calculateChanges loadClients ne nujen
    //fixme etot metod doljen tolko pereschitivat paginator
    this.paginationNum = Math.floor(this.calculateSliceNum(this.totalClients));
    console.log(this.calculateSliceNum(this.totalClients));

    this.tempPaginationArray = [];
    if ((this.clientRecordPhilter.paginationPage == 0 || this.clientRecordPhilter.paginationPage == 1) && this.paginationNum > 1) {
      let checkerNum = 3;
      if (this.paginationNum == 2) {
        checkerNum = 2;
      }
      this.addToTemPagArray(0, checkerNum);
      this.loadClients();
      return;
    }

    if (this.clientRecordPhilter.paginationPage > 1 && this.clientRecordPhilter.paginationPage < this.paginationNum - 2) {
      this.addToTemPagArray(this.clientRecordPhilter.paginationPage - 1, this.clientRecordPhilter.paginationPage + 2);
      this.loadClients();
      return;
    }

    if (this.clientRecordPhilter.paginationPage >= this.paginationNum - 3 && this.paginationNum > 2) {
      this.addToTemPagArray(this.paginationNum - 3, this.paginationNum);
      this.loadClients();
    }
  }

  addToTemPagArray(from: number, to: number) {
    for (let i = from; i < to; i++) {
      this.tempPaginationArray.push(i);
    }
  }

  //fixme page size changed
  sliceNumChanged() {
    this.loadClients();
    this.loadClientSlice(0);
  }

  //fixme name: pageChanged
  loadClientSlice(pageNum: number) {
    //fixme zachem pri smene stranici pereschityvat client count?
    this.loadPaginationNum();
    this.clientRecordPhilter.paginationPage = pageNum;

    //fixme calculateChanges est' v loadPaginationNum. zachem tut?
    this.calculateChanges();
  }

  sortBy(columnName: string) {
    if (this.clientRecordPhilter.columnName == columnName) {
      this.clientRecordPhilter.columnName = '-' + columnName;
    } else if (this.clientRecordPhilter.columnName == '-' + columnName) {
      this.clientRecordPhilter.columnName = 'empty';
    } else {
      this.clientRecordPhilter.columnName = columnName;
    }
    //fixme mojno prosto zagruzit client list?
    this.loadClientSlice(this.clientRecordPhilter.paginationPage);
  }

  deleteClient(deleteIndex: any) {
    this.httpService.delete("/client/delete", {
      index: this.clients[deleteIndex].id
    }).toPromise().then(result => {
      //fixme sdes' dojen srabotat' kak searchClicked
      console.log(this.clients[deleteIndex].id);
      this.loadClientSlice(this.clientRecordPhilter.paginationPage);
    }, error => {
      alert(error)
    });
  }

  increaseCurrentPagination() {
    this.clientRecordPhilter.paginationPage++;
    if (this.clientRecordPhilter.paginationPage > this.paginationNum - 1) {
      this.clientRecordPhilter.paginationPage = 0;
    }
    //fixme mojno proto vizivat' loadClients from server
    this.loadClientSlice(this.clientRecordPhilter.paginationPage);
  }

  decreaseCurrentPagination() {
    this.clientRecordPhilter.paginationPage--;
    if (this.clientRecordPhilter.paginationPage < 0) {
      this.clientRecordPhilter.paginationPage = this.paginationNum - 1;
    }
    //fixme mojno proto vizivat' loadClients from server
    this.loadClientSlice(this.clientRecordPhilter.paginationPage);
  }

  addNewClient(client: RecordClient) {
    if (this.notExistedClient(client.id)) {
      this.clients.pop();
      this.clients.unshift(client)
    } else {
      //todo prover' rabotaet li
      /*
      this.clients.forEach((value, index) => {
        if (value.id == client.id)
          this.clients[index] = client;
      });
      */
      for (let cl of this.clients) {
        if (cl.id == client.id) {
          cl.character = client.character;
          cl.name = client.name;
          cl.surname = client.surname;
          cl.age = client.age;
          cl.maxBalance = client.maxBalance;
          cl.minBalance = client.minBalance;
          cl.accBalance = client.accBalance;
        }
      }
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