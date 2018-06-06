import {Component, EventEmitter, Output, ViewChild} from "@angular/core";
import {HttpService} from "../HttpService";
import {RecordClient} from "../../model/RecordClient";
import {EditFormComponent} from "../edit_form/edit_form.component";
import {EditClient} from "../../model/EditClient";

const CLIENTS: {
    id: any,
    npmn: string,
    character: string,
    age: number,
    accBalance: number,
    maxBalance: number,
    minBalance: number
}[] = [];

@Component({
    selector: 'list-form-component',
    template: require("./list_form.component.html"),
    styles: [require('./list_form.component.css')],
})


export class ListFormComponent {

    @Output() openEditingForm = new EventEmitter<any>()

    @ViewChild(EditFormComponent) child;

    editingClient = null;

    currentColumnName = 'empty';
    currentPagination = 0;
    paginationNum = 10;

    clients = CLIENTS;
    modalFormVisible: boolean = false;
    searchWord = '';

    loadClients() {
        console.log(this.currentColumnName);
        this.httpService.get("/client/getClients", {
            columnName:
            this.currentColumnName + "",
            paginationPage:
            this.currentPagination + "",
            searchName:
            this.searchWord + "",
        }).toPromise().then(result => {
            let clients: RecordClient[] = [];
            for (let res of result.json()) {
                clients.push(res);
            }
            this.clearClientsList();
            this.pushToClientsList(clients);
            // this.loadTotalNumberOfPaginationPage();
            //

        }, error => {
            alert("Error   " + error.toString())
        });
    }

    clearClientsList() {
        while (CLIENTS.length > 0) {
            CLIENTS.pop();
        }
    }

    pushToClientsList(clients: RecordClient[]) {
        for (let arr of clients) {
            CLIENTS.push({
                "id": arr.id,
                "npmn": arr.surname + " " + arr.name + " " + arr.patronymic,
                "character": arr.character,
                "age": arr.age,
                "accBalance": arr.accBalance + 10,
                "maxBalance": arr.maxBalance + 10,
                "minBalance": arr.minBalance + 10
            });

            this.paginationNum = arr.paginationNum;
        }
    }

    tempPaginationArray = [];

    loadClientSlice(pagination: number) {

        this.currentPagination = pagination;

        if (this.currentPagination == 0 || this.currentPagination == 1) {
            while (this.tempPaginationArray.pop()) ;
            for (let i = 0; i < 3; i++) {
                this.tempPaginationArray.push(i);
            }
            this.loadClients();
            return;
        }

        if (this.currentPagination > 1 && this.currentPagination < this.paginationNum - 3) {
            while (this.tempPaginationArray.pop()) ;
            for (let i = this.currentPagination - 1; i <= this.currentPagination + 1; i++) {
                this.tempPaginationArray.push(i)
            }
            this.loadClients();
            return;
        }

        if (this.currentPagination >= this.paginationNum - 3) {
            while (this.tempPaginationArray.pop()) ;
            for (let i = this.paginationNum - 3; i < this.paginationNum; i++) {
                this.tempPaginationArray.push(i);
            }
            this.loadClients();
        }

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
        this.loadClients();
    }

    deleteClient(deleteIndex: any) {
        this.httpService.get("/client/delete", {
            index: this.clients[deleteIndex].id + ""
        }).toPromise().then(result => {
            this.loadClients();
        }, error => {
            alert(error)
        });
    }

    increaseCurrentPagination() {
        this.currentPagination++;
        if (this.currentPagination > this.paginationNum - 1) {
            this.currentPagination = 0;
        }
        this.loadClients();
    }

    decreaseCurrentPagination() {
        this.currentPagination--;
        if (this.currentPagination < 0) {
            this.currentPagination = this.paginationNum - 1;
        }
        this.loadClients();
    }

    addNewClient(client: EditClient) {
        if (client.id == null) {
            CLIENTS.pop();
            let recordClient: RecordClient = new RecordClient();
            recordClient.name = client.name;
            recordClient.surname = client.surname;
            recordClient.patronymic = client.patronymic;
            recordClient.maxBalance = 0;
            recordClient.minBalance = 0;
            recordClient.accBalance = 0;
            CLIENTS.unshift({
                "id": recordClient.id,
                "npmn": recordClient.surname + " " + recordClient.name + " " + recordClient.patronymic,
                "character": recordClient.character,
                "age": recordClient.age,
                "accBalance": recordClient.accBalance,
                "maxBalance": recordClient.maxBalance,
                "minBalance": recordClient.minBalance
            })
        } else {

        }
    }

    constructor(private httpService: HttpService) {
        this.loadClientSlice(0);
    }

}