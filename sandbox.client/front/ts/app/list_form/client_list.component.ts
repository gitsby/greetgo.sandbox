import {Component, ViewChild} from "@angular/core";
import {HttpService} from "../HttpService";
import {RecordClient} from "../../model/RecordClient";
import {EditFormComponent} from "../edit_form/edit_form.component";
import {ClientRecordPhilter} from "../../model/ClientRecordPhilter";

@Component({
  selector: 'list-form-component',
  template: require("./client_list.component.html"),
  styles: [require('./client_list.component.css')],
})
export class ClientListComponent {
  @ViewChild(EditFormComponent) child;

  openEditingClient: boolean = false;

  paginationNum = 10;

  clients: RecordClient[] = [];

  clientRecordPhilter: ClientRecordPhilter = new ClientRecordPhilter();

  tempPaginationArray = [];


  constructor(private httpService: HttpService) {
    this.loadPaginationNum();
    this.loadClients();
  }

  searchClicked() {
    if (this.clientRecordPhilter.searchName.length > 2 || this.clientRecordPhilter.searchName.length == 0) {
      this.loadClients();
      this.loadClientSlice(0);
    } else {
      alert("At least 3 symbols");
    }
  }

  plusClick() {
    this.openEditingClient = true;
  }

  editClick(index: any) {
    this.openEditingClient = true;
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
      this.paginationNum = this.calculateSliceNum(result.json());
      this.calculateChanges();
    }, error => {
      alert(error)
    })
  }

  calculateSliceNum(num: number): number {
    return num / this.clientRecordPhilter.sliceNum
      + ((num % this.clientRecordPhilter.sliceNum == 0) ? 0 : 1)
  }

  calculateChanges() {
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
      this.addToTemPagArray(this.paginationNum - 3, this.paginationNum)
      this.loadClients();
    }
  }

  addToTemPagArray(from: number, to: number) {
    for (let i = from; i < to; i++) {
      this.tempPaginationArray.push(i);
    }
  }

  sliceNumChanged() {
    this.loadClients();
    this.loadClientSlice(0);
  }

  loadClientSlice(pagination: number) {
    this.loadPaginationNum();
    this.clientRecordPhilter.paginationPage = pagination;
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
    this.loadClientSlice(this.clientRecordPhilter.paginationPage);
  }

  deleteClient(deleteIndex: any) {
    this.httpService.delete("/client/delete", {
      index: this.clients[deleteIndex].id
    }).toPromise().then(result => {
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
    this.loadClientSlice(this.clientRecordPhilter.paginationPage);
  }

  decreaseCurrentPagination() {
    this.clientRecordPhilter.paginationPage--;
    if (this.clientRecordPhilter.paginationPage < 0) {
      this.clientRecordPhilter.paginationPage = this.paginationNum - 1;
    }
    this.loadClientSlice(this.clientRecordPhilter.paginationPage);
  }

  addNewClient(client: RecordClient) {
    alert(client.name)
    if (this.notExistedClient(client.id)) {
      this.clients.pop();
      this.clients.unshift(client)
    } else {
      for (let cl of this.clients) {
        if (cl.id == client.id) {
          cl.character = client.character;
          cl.name = client.name;
          cl.surname = client.surname;
          cl.age = cl.age;
          cl.maxBalance = cl.maxBalance;
          cl.minBalance = cl.minBalance;
          cl.accBalance = cl.accBalance;
        }
      }
    }
  }

  notExistedClient(id: number): Boolean {
    for (let client of this.clients) {
      console.log(client.id + " " + id)
      if (client.id == id) {
        return false;
      }
    }
    return true;
  }

}