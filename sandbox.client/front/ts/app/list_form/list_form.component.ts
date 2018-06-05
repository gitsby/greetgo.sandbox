import {Component, EventEmitter, Output, ViewChild} from "@angular/core";
import {HttpService} from "../HttpService";
import {RecordClient} from "../../model/RecordClient";
import {EditFormComponent} from "../edit_form/edit_form.component";

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

    constructor(private httpService: HttpService) {
        this.loadClients();
        this.loadTotalNumberOfPaginationPage();
    }

    @ViewChild(EditFormComponent) child;

    editingClient = null;

    currentColumnName = 'empty';
    currentPagination = 0;
    paginationNum = 10;

    clients = CLIENTS;
    modalFormVisible: boolean = false;
    searchWord = '';


    loadTotalNumberOfPaginationPage() {
        this.httpService.get("/client/pagination_page_num").toPromise().then(
            result => {
                this.paginationNum = result.json();
            }, error => {
                alert(error)
            }
        );
    }

    loadClients() {
        console.log(this.currentColumnName);
        this.httpService.get("/client/sort", {
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

    loadClientSlice(index: number) {
        this.currentPagination = index;
        this.loadClients();
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

}