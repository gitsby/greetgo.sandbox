import {Component, EventEmitter, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {Client} from "../../model/Client";

const CLIENTS: {
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
    @Output() exit = new EventEmitter<void>();
    currentPagination = 0;
    paginationNum = 10;
    clients = CLIENTS;
    userInfo: UserInfo | null = null;
    loadUserInfoButtonEnabled: boolean = true;
    loadUserInfoError: string | null;

    constructor(private httpService: HttpService) {
        this.loadClients();
        this.loadTotalNumberOfPaginationPage();
    }

    loadTotalNumberOfPaginationPage() {
        this.httpService.get("/auth/pagination_page_num").toPromise().then(
            result => {
                this.paginationNum = result.json();
            }, error => {
                alert(error)
            }
        );
    }

    loadClients() {
        this.httpService.get("/auth/clients",
            {paginationPage: this.currentPagination + ""})
            .toPromise().then(result => {
            let clients: Client[] = [];
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

    pushToClientsList(clients: Client[]) {
        for (let arr of clients) {
            CLIENTS.push({
                "npmn": arr.snmn,
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

    plusClick(index: number) {
        alert(index);
    }

    sortBy(column: number) {
        alert(column);
    }

    searchFromInput() {
        let stringToSearch = (document.getElementById("input") as HTMLInputElement).value;

        this.httpService.get("/auth/search", {searchName: stringToSearch + ""})
            .toPromise().then(result => {
            alert((result.json() as Client).snmn)
            let clients: Client[] = [];
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
        this.httpService.get("/auth/delete", {
            index: deleteIndex + "",
            paginationPage: this.currentPagination as any
        }).toPromise().then(result => {
            this.loadClients();
        }, error => {
            alert(error)
        });
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
