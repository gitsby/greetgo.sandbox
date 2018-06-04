import {Component, EventEmitter, Output, ViewChild} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
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
const sliceNum: number = 10;

@Component({
    selector: 'main-form-component',
    template: require("./main_form.component.html"),
    styles: [require('./main_form.component.css')],
})

export class MainFormComponent {

    @ViewChild(EditFormComponent) child;

    public mvar = 10;
    @Output() exit = new EventEmitter<void>();

    editingClient = null;

    currentColumnNum = 0;
    currentPagination = 0;
    paginationNum = 10;

    clients = CLIENTS;
    userInfo: UserInfo | null = null;
    loadUserInfoButtonEnabled: boolean = true;
    loadUserInfoError: string | null;
    modalFormVisible: boolean = false;

    constructor(private httpService: HttpService) {
        this.loadClients();
        let modal = document.getElementById("myModal");
        this.loadTotalNumberOfPaginationPage();

        window.onclick = function (event) {
            if (event.target == modal) {
                document.getElementById("myModal").style.display = "none";
            }
        }
    }

    setEditingClientNull() {
        this.editingClient = null;
    }

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
        this.httpService.get("/client/sort", {
            columnNum:
            this.currentColumnNum + "",
            paginationPage:
            this.currentPagination + ""
        }).toPromise().then(result => {
            let clients: RecordClient[] = [];
            for (let res of result.json()) {
                clients.push(res);
            }
            this.clearClientsList();
            this.pushToClientsList(clients);
            this.loadTotalNumberOfPaginationPage();

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
        }
    }

    loadClientSlice(index: number) {
        this.currentPagination = index;
        this.loadClients();
    }

    plusClick() {
        this.child.client = null;
        this.modalFormVisible = true;
        this.editingClient = -1;
    }

    editClick(index: any) {
        // this.child.client = this.clients[index].id;
        this.editingClient = this.clients[index].id + "";
        this.child.loadFromDatabase(this.clients[index].id);
    }

    sortBy(columnNum: any) {
        if (this.currentColumnNum == columnNum) {
            this.currentColumnNum = 0;
        } else {
            this.currentColumnNum = columnNum;
        }
        this.httpService.get("/client/sort", {
            columnNum:
            columnNum + "",
            paginationPage:
            this.currentPagination + ""
        }).toPromise().then(result => {
            let clients: RecordClient[] = [];
            for (let res of result.json()) {
                clients.push(res);
            }
            this.clearClientsList();
            this.pushToClientsList(clients);
            this.loadTotalNumberOfPaginationPage();
        }, error => {
            alert(error);
        })
    }

    searchFromInput() {
        let stringToSearch = (document.getElementById("input") as HTMLInputElement).value;

        this.httpService.get("/client/search", {searchName: stringToSearch + ""})
            .toPromise().then(result => {
            let clients: RecordClient[] = [];
            for (let client of result.json()) {
                clients.push(client);
            }
            this.clearClientsList();
            this.pushToClientsList(clients);
        }, error => {
            alert("Error " + error)
        });
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
        alert(this.currentPagination + " " + this.paginationNum)
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

    loadUserInfoButtonClicked() {
        this.loadUserInfoButtonEnabled = false;
        this.loadUserInfoError = null;

        this.httpService.get("/auth/userInfo",
            {paginationPage: this.currentPagination})
            .toPromise().then(result => {
            this.userInfo = UserInfo.copy(result.json());
            let phoneType: PhoneType | null = this.userInfo.phoneType;
            console.log(phoneType);
        }, error => {
            console.log(error);
            this.loadUserInfoButtonEnabled = true;
            this.loadUserInfoError = error;
            this.userInfo = null;
        });
    }
}
