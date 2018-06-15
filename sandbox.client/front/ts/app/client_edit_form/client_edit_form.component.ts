import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from "@angular/core";
import {HttpService} from "../HttpService";
import {ClientToSave} from "../../model/ClientToSave";
import {Charm} from "../../model/Charm";
import {Gender} from "../../model/Gender";
import {Details} from "../../model/Details";

@Component({
  selector: 'client-info-form-component',
  template: require('./client_edit_form.component.html'),
  styles: [require('./client_edit_form.component.css')],
})
export class ClientEditFormComponent implements OnInit {
  @Input() clientId: number;
  @Output() onClose = new EventEmitter<boolean>();

  @ViewChild('birthDayInput') birthDayInput: ElementRef;

  title = "Новый клиент";
  buttonTitle = "Добавить";

  charms: Array<Charm> = [];
  wrongMessageEnable: boolean = false;
  clientToSave: ClientToSave = new ClientToSave();

  currentDate: Date = new Date();

  constructor(private httpService: HttpService) {
    this.loadCharms();
  }

  ngOnInit(): void {

    if (this.clientId != null) {
      this.title = "Изменить данные клиента";
      this.buttonTitle = "Изменить";
      let clientId = this.clientId as number;

      this.httpService.get("/client/details", {"clientId": clientId}).toPromise().then(result => {
        console.log(result.json());
        this.clientToSave = Details.copy(result.json()).toClientToSave();
        console.log(this.clientToSave);
        this.formatAllPhoneNumbers();
      })
    }
    else {
      this.clientToSave = new ClientToSave();
      this.clientToSave.gender = Gender.MALE;
      this.clientToSave.charmId = 1;
    }
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
      this.isCorrectDate(this.clientToSave.birthDate) &&
      this.checkClientText() &&
      this.checkClientAddress() &&
      !ClientEditFormComponent.isEmpty(this.clientToSave.birthDate) &&
      !ClientEditFormComponent.isEmpty(this.clientToSave.gender)) {
      this.reformatAllPhoneNumbers();
      return true;
    }
    return false;
  }

  checkClientAddress(): boolean {
    if (ClientEditFormComponent.isEmpty(this.clientToSave.addressReg)) return false;
    let re = /^[a-zA-Z0-9а-яА-Я_]+$/;
    return this.isCorrect(re, this.clientToSave.addressReg.street) &&
      this.isCorrect(re, this.clientToSave.addressReg.house) &&
      this.isCorrect(re, this.clientToSave.addressReg.flat);
  }

  checkClientText(): boolean {
    let re = /^[a-zA-Zа-яА-Я_]*$/;
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

  reformatAllPhoneNumbers() {
    this.clientToSave.homePhone.number = this.reformatPhoneNumber(this.clientToSave.homePhone.number);
    this.clientToSave.mobilePhone.number = this.reformatPhoneNumber(this.clientToSave.mobilePhone.number);
    this.clientToSave.workPhone.number = this.reformatPhoneNumber(this.clientToSave.workPhone.number);
  }

  static isEmpty(element: any): boolean {
    return (element == null || element == "")
  }

  saveButtonClicked() {
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

  setBDate(dateText: string) {
    if (dateText.length < 10) return;
    this.clientToSave.birthDate = new Date(dateText);
  }

  isCorrectDate(date: Date): boolean {
    return date.getTime() < new Date().getTime() && date.getTime() > new Date("1000-01-01").getTime();
  }


  formatInputText(id: number) {
    switch (id) {
      case 0:
        this.clientToSave.name = ClientEditFormComponent.formatText(this.clientToSave.name);
        break;
      case 1:
        this.clientToSave.surname = ClientEditFormComponent.formatText(this.clientToSave.surname);
        break;
      case 2:
        this.clientToSave.patronymic = ClientEditFormComponent.formatText(this.clientToSave.patronymic);
        break;
    }
  }

  static formatText(clientText: string | null): string {
    if (clientText == null) return "";
    return clientText.replace(/[\*\+\^\&\$\#\%\@\)\(\_\+\=\!\s_\-\d]+/g, '');
  }

  formatAllPhoneNumbers() {
    if (this.clientToSave.homePhone.number != null) this.formatInputNumber(0);
    if (this.clientToSave.workPhone.number) this.formatInputNumber(1);
    if (this.clientToSave.mobilePhone.number) this.formatInputNumber(2);
  }

  formatInputNumber(id: number) {
    switch (id) {
      case 0:
        this.clientToSave.homePhone.number = ClientEditFormComponent.formatPhoneNumber(this.clientToSave.homePhone.number);
        break;
      case 1:
        this.clientToSave.workPhone.number = ClientEditFormComponent.formatPhoneNumber(this.clientToSave.workPhone.number);
        break;
      case 2:
        this.clientToSave.mobilePhone.number = ClientEditFormComponent.formatPhoneNumber(this.clientToSave.mobilePhone.number);
        break;
    }
  }

  static formatPhoneNumber(clientNumber: string | null): string {
    if (clientNumber == null) return "";

    let number = clientNumber.replace(/[\W\s._\-a-zA-Z]+/g, '');

    if (number.length > 11) {
      number = number.substr(0, 11);
    }
    let split = 4;
    let chunk = [];
    for (let i = 0, len = number.length; i < len; i += split) {
      split = (i <= 0) ? 1 : ((i >= 7) ? 2 : 3);
      chunk.push(number.substr(i, split));
    }
    return chunk.join("-").toUpperCase();
  }

  reformatPhoneNumber(number: string | null): string {
    if (number == null) return "";
    return number.replace(/[($)\W\s._\-]+/g, '');
  }
}