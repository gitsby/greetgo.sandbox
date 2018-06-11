import {Component, EventEmitter, Output, ViewChild} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {EditFormComponent} from "../edit_form/edit_form.component";
import {ClientListComponent} from "../list_form/client_list.component";
import {RecordClient} from "../../model/RecordClient";

@Component({
    selector: 'main-form-component',
    template: require("./main_form.component.html"),
    styles: [require('./main_form.component.css')],
})

export class MainFormComponent {
    clientsText = "Open Clients";
    userInfoText = "Load User Data"
    openClient = null;
    editingClient = null;
    userInfo: UserInfo | null = null;
    loadUserInfoButtonEnabled: boolean = true;
    loadUserInfoError: string | null;

    @ViewChild(EditFormComponent) child;
    @ViewChild(ClientListComponent) listForm;

    @Output() exit = new EventEmitter<void>();


    constructor(private httpService: HttpService) {

    }

    openEditingForm(clientId: string) {
        this.editingClient = clientId;
        if (this.editingClient.toString() != ' ') {
            this.child.loadFromDatabase(clientId);
        }
    }

    setEditingClientNull() {
        this.editingClient = null;
    }


    openClients() {
        if (!this.openClient) {
            this.clientsText = "Hide Clients"
            this.openClient = 'open';
        } else {
            this.clientsText = "Open Clients"
            this.openClient = null;
        }
    }

    applyChanges(editedClient: RecordClient) {
        this.listForm.addNewClient(editedClient);
    }

    loadUserInfoButtonClicked() {
        this.loadUserInfoError = null;
        if (this.userInfo != null) {
            this.userInfoText = "Load User Data";
            this.loadUserInfoButtonEnabled = true;
            this.userInfo = null;
            return;
        }
        this.userInfoText = "Hide User Data";
        this.loadUserInfoButtonEnabled = false;
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
