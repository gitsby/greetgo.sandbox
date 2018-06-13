import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientToSave} from "../../model/ClientToSave";
import {Charm} from "../../model/Charm";
import {Gender} from "../../model/Gender";

@Component({
  selector: 'client-info-form-component',
  template: require('./client_edit_form.component.html'),
  styles: [require('./client_edit_form.component.css')],
})
export class ClientEditFormComponent implements OnInit{
  @Input() clientId: number;
  @Output() onClose = new EventEmitter<boolean>();

  @ViewChild('birthDayInput') birthDayInput: ElementRef;

  title = "Новый клиент";
  buttonTitle = "Добавить";

  charms: Array<Charm> = [];
  wrongMessageEnable: boolean = false;
  clientToSave: ClientToSave = new ClientToSave();

  constructor(private httpService: HttpService) {
    this.loadCharms();
  }

  ngOnInit(): void {
    if (this.clientId != null) {
      this.title = "Изменить данные клиента";
      this.buttonTitle = "Изменить";
      let clientId = this.clientId as number;
      this.httpService.get("/client/detail", {"clientId": clientId}).toPromise().then(result => {
        this.clientToSave = ClientToSave.copy(result.json());
        this.serializeAllNumber();
        console.log(this.clientToSave)
      })
    }
    else {
      this.clientToSave = new ClientToSave();
      this.clientToSave.gender = Gender.MALE;
      this.clientToSave.charmId = 1;
    }
    this.setMaxDate();
  }

  loadCharms() {
    this.httpService.get("/client/getCharms").toPromise().then(result => {
      for (let res of result.json())
        this.charms.push(Charm.copy(res));
      console.log(this.charms);

    })
  }

  checkClientData(): boolean {
    if (this.checkClientPhones() &&
      this.checkClientText() &&
      this.checkClientAddresse() &&
      !ClientEditFormComponent.isEmpty(this.clientToSave.birth_day) &&
      !ClientEditFormComponent.isEmpty(this.clientToSave.gender)) {
      this.unSerializeAllNumbers();
      return true;
    }
    return false;
  }

  checkClientAddresse(): boolean{
    if (ClientEditFormComponent.isEmpty(this.clientToSave.addressReg)) return false;
    let re = /^[a-zA-Z0-9_]+$/;
    return this.isCorrect(re, this.clientToSave.addressReg.street) &&
      this.isCorrect(re, this.clientToSave.addressReg.house) &&
      this.isCorrect(re, this.clientToSave.addressReg.flat);
  }

  checkClientText(): boolean {
    let re = /^[a-zA-Z_]*$/;
    return this.isCorrect(re, this.clientToSave.name)
      && this.isCorrect(re, this.clientToSave.surname);
  }

  checkClientPhones(): boolean {
    let re = /^[0-9]-[0-9]{3}-[0-9]{3}-[0-9]{2}-[0-9]{2}$/;
    return this.isCorrect(re, this.clientToSave.homePhone.number)
      && this.isCorrect(re, this.clientToSave.workPhone.number)
      && this.isCorrect(re, this.clientToSave.mobilePhone.number);
  }

  isCorrect(re: RegExp, text: string): boolean {
    if (ClientEditFormComponent.isEmpty(text)) return false;
    return text.match(re) != null;
  }

  unSerializeAllNumbers() {
    this.clientToSave.homePhone.number = this.unSerializeNumber(this.clientToSave.homePhone.number);
    this.clientToSave.mobilePhone.number = this.unSerializeNumber(this.clientToSave.mobilePhone.number);
    this.clientToSave.workPhone.number = this.unSerializeNumber(this.clientToSave.workPhone.number);
  }

  static isEmpty(element: any): boolean {
    return (element == null || element == "")
  }

  saveButtonClicked () {
    if (this.checkClientData()) {
      this.wrongMessageEnable = false;
      this.saveClient();
    }
    else {
      this.wrongMessageEnable = true;
    }
  }

  saveClient() {
    this.httpService.post("/client/save", {
      "clientToSave": JSON.stringify(this.clientToSave)
    }).toPromise().then(() => {
      this.onClose.emit(true);
    })
  }

  closeButtonClicked(clientSaved: boolean) {
    this.onClose.emit(clientSaved);
  }

  setBDate(dateText) {
    this.clientToSave.birth_day = new Date(dateText);
  }

  setMaxDate() {
    let today = new Date();
    let dd:string = today.getDate().toString();
    let mm:string = (today.getMonth()+1).toString(); //January is 0!
    let yyyy: string = today.getFullYear().toString();
    if(dd.length<10){
      dd='0'+dd
    }
    if(mm.length<10){
      mm='0'+mm
    }
    this.birthDayInput.nativeElement.setAttribute("max", yyyy+'-'+mm+'-'+dd);
  }

  serializeInputText(id: number) {
    switch (id) {
      case 0:
        this.clientToSave.name = ClientEditFormComponent.serializeText(this.clientToSave.name);
        break;
      case 1:
        this.clientToSave.surname = ClientEditFormComponent.serializeText(this.clientToSave.surname);
        break;
      case 2:
        this.clientToSave.patronymic = ClientEditFormComponent.serializeText(this.clientToSave.patronymic);
        break;
    }
  }

  static serializeText(clientText: string | null): string {
    if (clientText == null) return "";
    return clientText.replace(/[\W\s_\-\d]+/g, '');
  }

  serializeAllNumber() {
    if (this.clientToSave.homePhone.number != null) this.serializeInputNumber(0);
    if (this.clientToSave.workPhone.number) this.serializeInputNumber(1);
    if (this.clientToSave.mobilePhone.number) this.serializeInputNumber(2);
  }

  serializeInputNumber(id: number) {
    switch (id) {
      case 0:
        this.clientToSave.homePhone.number = ClientEditFormComponent.serializeNumber(this.clientToSave.homePhone.number);
        break;
      case 1:
        this.clientToSave.workPhone.number = ClientEditFormComponent.serializeNumber(this.clientToSave.workPhone.number);
        break;
      case 2:
        this.clientToSave.mobilePhone.number = ClientEditFormComponent.serializeNumber(this.clientToSave.mobilePhone.number);
        break;
    }
  }

  static serializeNumber(clientNumber: string | null): string {
    if (clientNumber == null) return "";

    let number = clientNumber.replace(/[\W\s._\-a-zA-Z]+/g, '');

    if (number.length > 11) {
      number = number.substr(0, 11);
    }
    let split = 4;
    let chunk = [];
    for (let i = 0, len = number.length; i < len; i += split) {
      split = ( i <= 0 ) ? 1 : ((i >= 7) ? 2 : 3);
      chunk.push( number.substr( i, split ) );
    }
    return chunk.join("-").toUpperCase();
  }

  unSerializeNumber(number: string | null): string {
    if (number == null) return "";
    return number.replace(/[($)\W\s._\-]+/g, '');
  }
}