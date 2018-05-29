import {Component, EventEmitter, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {Client} from "../../model/Client";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MatTable, MatSortModule} from "@angular/material"

const STRINGS: {
    npmn: string,
    character: string,
    age: number,
    accBalance: number,
    maxBalance: number,
    minBalance: number
}[] = [];


@Component({
    selector: 'main-form-component',
    template: require("./main_form.component.html"),
    styles: [require('./main_form.component.css')],
})
export class MainFormComponent {
    @Output() exit = new EventEmitter<void>();

    clients = STRINGS;
    userInfo: UserInfo | null = null;
    loadUserInfoButtonEnabled: boolean = true;
    loadUserInfoError: string | null;

    constructor(private httpService: HttpService) {
        this.loadClients();
    }

    loadClients() {
        this.httpService.get("/auth/clients").toPromise().then(result => {
            let clients: Client[] = [];
            for (let res of result.json()) {
                clients.push(res);
            }
            for (let arr of clients) {
                STRINGS.push({
                    "npmn": arr.snmn,
                    "character": arr.character,
                    "age": arr.age,
                    "accBalance": arr.accBalance + 10,
                    "maxBalance": arr.maxBalance + 10,
                    "minBalance": arr.minBalance + 10
                });
            }
        }, error => {
            alert("Error   " + error.toString())
        });
    }

    loadUserInfoButtonClicked() {
        this.loadUserInfoButtonEnabled = false;
        this.loadUserInfoError = null;

        this.httpService.get("/auth/userInfo").toPromise().then(result => {
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

    plusClick(index: number) {
        alert(index);
    }

    deleteClient(index: number) {
        STRINGS.splice(index, 1);
    }
}
