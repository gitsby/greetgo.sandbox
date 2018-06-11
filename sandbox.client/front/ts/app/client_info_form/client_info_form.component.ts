import {Component, ElementRef, EventEmitter, Input, OnInit, Output, QueryList, ViewChildren} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientToSave} from "../../model/ClientToSave";
import {Charm} from "../../model/Charm";

@Component({
  selector: 'client-info-form-component',
  template: require('./client_info_form.component.html'),
  styles: [require('./client_info_form.component.css')],
})
export class ClientInfoFormComponent implements OnInit{
  @Input() clientId: number;
  @Output() onClose = new EventEmitter<boolean>();

  @ViewChildren('valid_input') valid_input: QueryList<ElementRef>;

  charms: Array<Charm> = new Array<Charm>();
  wrongMessageEnable: boolean = false;
  clientToSave: ClientToSave = new ClientToSave();

  constructor(private httpService: HttpService) {
    this.loadCharms();
  }

  ngOnInit(): void {
    if (this.clientId != null) {
      let clientId = this.clientId as number;
      this.httpService.get("/client/detail", {"clientId": clientId}).toPromise().then(result => {
        this.clientToSave = ClientToSave.copy(result.json());
        console.log(this.clientToSave);
      })
    }

    else
      this.clientToSave = new ClientToSave();
  }

  loadCharms() {
    this.httpService.get("/client/getCharms").toPromise().then(result => {
      for (let res of result.json())
        this.charms.push(Charm.copy(res));
      console.log(this.charms);
    })
  }

  checkClient(): boolean {
    return this.checkData(this.clientToSave.addressReg) &&
      this.checkClientPhones() &&
      this.checkData(this.clientToSave.name) &&
      this.checkData(this.clientToSave.surname) &&
      this.checkData(this.clientToSave.birth_day) &&
      this.checkData(this.clientToSave.gender) &&
      this.checkClientPhones();
  }

  checkData(element: any): boolean {
    return (element != null && element != "")
  }

  checkClientPhones(): boolean {
    return this.checkData(this.clientToSave.mobilePhone.number) &&
      this.checkData(this.clientToSave.workPhone.number) &&
      this.checkData(this.clientToSave.homePhone.number);
  }

  saveButtonClicked () {
    if (this.checkClient()) {
      this.wrongMessageEnable = false;
      //this.saveClient();
    }
    else {
      this.wrongMessageEnable = true;
    }
  }

  saveClient() {
    console.log("CLIENT TO SAVE="+this.clientToSave);
    this.httpService.post("/client/save", {
      "clientToSave": JSON.stringify(this.clientToSave)
    }).toPromise().then(result => {
      this.onClose.emit(true);
    })
  }

  closeButtonClicked(clientSaved: boolean) {
    this.onClose.emit(clientSaved);
  }

  setBDate(dateText) {
    this.clientToSave.birth_day = new Date(dateText);
  }
}