import {Component, EventEmitter, Output, ViewChild} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {EditFormComponent} from "../edit_form/edit_form.component";
import {ListFormComponent} from "../list_form/list_form.component";

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
    selector: 'main-form-component',
    template: require("./main_form.component.html"),
    styles: [require('./main_form.component.css')],
})

export class MainFormComponent {

    searchField = '';

    editingClient = null;
    userInfo: UserInfo | null = null;
    loadUserInfoButtonEnabled: boolean = true;
    loadUserInfoError: string | null;

    @ViewChild(EditFormComponent) child;
    @ViewChild(ListFormComponent) listForm;

    @Output() exit = new EventEmitter<void>();


    constructor(private httpService: HttpService) {

    }

    openEditingForm(clientId: string) {
        this.editingClient = clientId;
        this.child.loadFromDatabase(clientId);
    }

    setEditingClientNull() {
        this.editingClient = null;
    }

    searchClicked() {
        this.listForm.searchWord = this.searchField;
        this.listForm.loadClients();
    }

    plusClick() {
        this.editingClient = ' ';
    }

    loadUserInfoButtonClicked() {
        this.loadUserInfoButtonEnabled = false;
        this.loadUserInfoError = null;

        this.httpService.get("/auth/userInfo")
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
