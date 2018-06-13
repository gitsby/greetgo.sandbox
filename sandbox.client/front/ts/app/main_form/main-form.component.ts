import {Component, EventEmitter, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";

@Component({
  selector: 'main-form-component',
  template: require("./main-form.component.html"),
  styles: [require('./main-form.component.css')],
})

export class MainFormComponent {
  clientsText = "Open Clients";
  userInfoText = "Load User Data";
  openClient = null;

  userInfo: UserInfo | null = null;
  loadUserInfoButtonEnabled: boolean = true;
  loadUserInfoError: string | null;

  @Output() exit = new EventEmitter<void>();


  constructor(private httpService: HttpService) {

  }


  openClients() {
    if (!this.openClient) {
      this.clientsText = "Hide Clients";
      this.openClient = 'open';
    } else {
      this.clientsText = "Open Clients";
      this.openClient = null;
    }
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
