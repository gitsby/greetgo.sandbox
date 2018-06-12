import {Component, EventEmitter, Output, ViewChild} from "@angular/core";
import {HttpService} from "../HttpService";
import {RecordClient} from "../../model/RecordClient";
import {EditFormComponent} from "../edit_form/edit_form.component";

@Component({
  selector: 'list-form-component',
  template: require("./client_list.component.html"),
  styles: [require('./client_list.component.css')],
})
//fixme Удали код, который не будет использоваться и сделай проще
export class ClientListComponent {

  @Output() openEditingForm = new EventEmitter<any>();

  @ViewChild(EditFormComponent) child;

  editingClient = null;

  currentColumnName = 'empty';
  currentPagination = 0;
  paginationNum = 10;

  clients: RecordClient[] = [];
  sliceNum = 10;
  searchWord = '';


  constructor(private httpService: HttpService) {
    this.loadPaginationNum();
    this.loadClients();
  }

  searchClicked() {
    if (this.searchWord.length > 2 || this.searchWord.length == 0) {
      this.loadClients();
      this.loadClientSlice(0);
    } else {
      alert("At least 3 symbols");
    }
  }

  plusClick() {
    this.editingClient = ' ';
    this.openEditingForm.emit(this.editingClient);
  }

  loadClients() {
    console.log(this.currentPagination + " " + this.paginationNum)
    this.httpService.get("/client/getClients", {
      columnName:
      this.currentColumnName + "",
      paginationPage:
      this.currentPagination + "",
      searchName:
      this.searchWord + "",
      sliceNum:
      this.sliceNum,
    }).toPromise().then(result => {
      this.clearClientsList();
      for (let res of result.json()) {
        this.clients.push(res);
      }
      //this.pushToClientsList(clients);
      // this.loadTotalNumberOfPaginationPage();
      //

    }, error => {
      alert("Error   " + error.toString())
    });
  }

  pagReceived: boolean = false;

  loadPaginationNum() {
    this.httpService.get("/client/getPaginationNum", {
      searchText: this.searchWord,
      sliceNum: this.sliceNum
    }).toPromise().then(result => {
      this.pagReceived = true;
      this.paginationNum = result.json();
      this.calculateChanges();
    }, error => {
      alert(error)
    })
  }

  calculateChanges() {
    //fixme this.tempPaginationArray = []; !?!
    while (this.tempPaginationArray.pop()) ;
    if ((this.currentPagination == 0 || this.currentPagination == 1) && this.paginationNum > 1) {
      let checkerNum = 3;
      console.log("Current pag" + this.paginationNum);
      if (this.paginationNum == 2) {
        checkerNum = 2;
      }
      for (let i = 0; i < checkerNum; i++) {

        this.tempPaginationArray.push(i);
      }
      this.loadClients();
      this.pagReceived = false;
      return;
    }

    if (this.currentPagination > 1 && this.currentPagination < this.paginationNum - 2) {

      for (let i = this.currentPagination - 1; i <= this.currentPagination + 1; i++) {
        this.tempPaginationArray.push(i)
      }
      this.loadClients();
      this.pagReceived = false;
      return;
    }

    if (this.currentPagination >= this.paginationNum - 3 && this.paginationNum > 2) {
      for (let i = this.paginationNum - 3; i < this.paginationNum; i++) {
        this.tempPaginationArray.push(i);
      }
      this.pagReceived = false;
      this.loadClients();
    }
  }


  clearClientsList() {
    this.clients = [];
  }

  tempPaginationArray = [];

  changed() {
    this.loadClients();
    this.loadClientSlice(0);
  }

  loadClientSlice(pagination: number) {
    this.loadPaginationNum();
    this.currentPagination = pagination;
    this.calculateChanges();

  }

  editClick(index: any) {
    // this.child.client = this.clients[index].id;
    this.editingClient = this.clients[index].id + "";
    this.openEditingForm.emit(this.editingClient);
  }

  sortBy(columnName: string) {
    if (this.currentColumnName == columnName) {
      this.currentColumnName = '-' + columnName;
    } else if (this.currentColumnName == '-' + columnName) {
      this.currentColumnName = 'empty';
    } else {
      this.currentColumnName = columnName;
    }
    this.loadClientSlice(this.currentPagination);
  }

  deleteClient(deleteIndex: any) {
    this.httpService.delete("/client/delete", {
      index: this.clients[deleteIndex].id + ""
    }).toPromise().then(result => {
      console.log(this.clients[deleteIndex].id);
      this.loadClientSlice(this.currentPagination);
    }, error => {
      alert(error)
    });
  }

  increaseCurrentPagination() {
    this.currentPagination++;
    if (this.currentPagination > this.paginationNum - 1) {
      this.currentPagination = 0;
    }
    this.loadClientSlice(this.currentPagination);
  }

  decreaseCurrentPagination() {
    this.currentPagination--;
    if (this.currentPagination < 0) {
      this.currentPagination = this.paginationNum - 1;
    }
    this.loadClientSlice(this.currentPagination);
  }

  addNewClient(client: RecordClient) {
    if (this.notExistedClient(client.id)) {
      this.clients.pop();
      this.clients.unshift(client)
    } else {
      for (let cl of this.clients) {
        if (cl.id == client.id) {
          cl.character = client.character;
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