import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {Component, Inject} from "@angular/core";
import {HttpService} from "../HttpService";
import "rxjs-compat/add/observable/of";
import {ClientDetail} from "../models/client.detail";
import {Charm} from "../models/charm";
import {ClientRecord} from "../models/client.record";

@Component({
    selector: 'edit-dialog',
    template: require('./edit.dialog.component.html'),
    styles: [require('./edit.dialog.component.css')],
})
export class DialogComponent {

    constructor(public dialogRef: MatDialogRef<DialogComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any, private http: HttpService) {
        this.http.get("/client/get_charms").toPromise().then(result => {
            this.charmList = [];
            for (let i = 0; i < Number(JSON.stringify(result.json().length)); i++) {
                let charm = new Charm(result.json()[i]);
                this.charmList.push(charm);
            }
        }, error => {
            alert("Error " + error);
        });
    }

    date: Date;
    clientDetail: ClientDetail;
    charmList: Charm[] = [];

    ngOnInit() {
        this.clientDetail = new ClientDetail(null);

        if (this.data.whichDialogNeeded == 2) {
            this.getClientDetailById(this.data.clientId);
        } else {
            this.date = new Date();
        }
    }

    start() {
        if (this.isValid()) { // TODO edit
            console.log('valid');
            if (this.data.whichDialogNeeded == 2) {
                this.edit(this.data.clientId);
            } else {
                this.add();
            }
        } else
            console.log('invalid');
    }

    edit(clientId) {
        this.http.post("/client/edit_client", {
            clientToSave: this.generateClientToSave(clientId)
        }).toPromise().then(result => {
            let clientRecord = new ClientRecord(result.json());
            this.dialogRef.close(clientRecord);
        }, error => {
            alert("Error " + error);
        });

    }

    getClientDetailById(clientId) {
        this.http.post("/client/get_client_info_by_id", {
            clientId: clientId
        }).toPromise().then(result => {
            this.clientDetail = new ClientDetail(result.json());
            this.date = new Date(this.clientDetail.birth_date);
        }, error => {
            alert("Error " + error);
        });
    }

    add() {
        this.http.post("/client/add_new_client", {
            clientToSave: this.generateClientToSave(null)
        }).toPromise().then(result => {
            let clientRecord = new ClientRecord(result.json());
            this.dialogRef.close(clientRecord);
        }, error => {
            alert("Error " + error);
        });
    }

    generateClientToSave(clientId): ClientDetail {
        let id: number = null;
        if (clientId != null)
            id = clientId;
        this.clientDetail.id = id;
        this.clientDetail.birth_date = this.date.getMonth() + "/" + this.date.getDay() + "/" + this.date.getFullYear();
        return this.clientDetail;
    }

    isValid(): boolean {
        if (this.clientDetail.name != null && this.clientDetail.name.length != 0 &&
            this.clientDetail.surname != null && this.clientDetail.surname.length != 0 &&
            this.clientDetail.gender != null && this.clientDetail.gender.length != 0 &&
            this.clientDetail.charm != null &&
            this.clientDetail.phoneHome != null && this.isPhoneValid(this.clientDetail.phoneHome) &&
            this.clientDetail.addrRegStreet != null && this.clientDetail.addrRegStreet.length != 0 &&
            this.clientDetail.addrRegHome != null && this.clientDetail.addrRegHome.length != 0 &&
            this.clientDetail.addrRegFlat != null && this.clientDetail.addrRegFlat.length != 0) {
            let count: number = 0;
            let temp: number = 0;
            if (this.clientDetail.phoneWork != null) {
                count++;
                if (this.isPhoneValid(this.clientDetail.phoneWork)) temp++;
            }
            if (this.clientDetail.phoneMob1 != null) {
                count++;
                if (this.isPhoneValid(this.clientDetail.phoneMob1)) temp++;
            }
            if (this.clientDetail.phoneMob2 != null) {
                count++;
                if (this.isPhoneValid(this.clientDetail.phoneMob2)) temp++;
            }
            if (this.clientDetail.phoneMob3 != null) {
                count++;
                if (this.isPhoneValid(this.clientDetail.phoneMob3)) temp++;
            }
            return count == temp;

        } else return false;
    }

    isPhoneValid(phone: string): boolean {
        let reg = new RegExp('^(1[ \\-\\+]{0,3}|\\+1[ -\\+]{0,3}|\\+1|\\+)?((\\(\\+?1-[2-9][0-9]{1,2}\\))|(\\(\\+?[2-8][0-9][0-9]\\))|(\\(\\+?[1-9][0-9]\\))|(\\(\\+?[17]\\))|(\\([2-9][2-9]\\))|([ \\-\\.]{0,3}[0-9]{2,4}))?([ \\-\\.][0-9])?([ \\-\\.]{0,3}[0-9]{2,4}){2,3}$');
        return reg.test(phone);
    }

}