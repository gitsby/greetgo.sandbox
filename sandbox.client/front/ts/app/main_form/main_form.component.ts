import {Component, EventEmitter, Output} from "@angular/core";
import {UserInfo} from "../../model/UserInfo";
import {HttpService} from "../HttpService";
import {PhoneType} from "../../model/PhoneType";
import {TableModel} from "../../model/TableModel";
import {User} from "../../model/User";
import {Phone} from "../../model/Phone";
import {Address} from "../../model/Address";
import {CharmType} from "../../model/CharmType";
import {AddressType} from "../../model/AddressType";

@Component({
  selector: 'main-form-component',
  template: `
    <div>
      <h2>Main Form Component</h2>

      <button (click)="exit.emit()">Выход</button>

      <div *ngIf="!userInfo">
        <button [disabled]="!loadUserInfoButtonEnabled" (click)="loadUserInfoButtonClicked()">
          Загрузить данные пользователя
        </button>
        <div *ngIf="loadUserInfoError">
          {{loadUserInfoError}}
        </div>
      </div>
      <div>
          <button (click)="loadMockRequestClicked()">
              mockRequest
          </button>
      </div>
        
      <div *ngIf="userInfo">

        <table>
          <tbody>

          <tr>
            <td>ID</td>
            <td>&nbsp;:&nbsp;</td>
            <td><b>{{userInfo.id}}</b></td>
          </tr>
          <tr>
            <td>Account name</td>
            <td>&nbsp;:&nbsp;</td>
            <td><b>{{userInfo.accountName}}</b></td>
          </tr>
          <tr>
            <td>Surname</td>
            <td>&nbsp;:&nbsp;</td>
            <td><b>{{userInfo.surname}}</b></td>
          </tr>
          <tr>
            <td>Name</td>
            <td>&nbsp;:&nbsp;</td>
            <td><b>{{userInfo.name}}</b></td>
          </tr>
          <tr>
            <td>Patronymic</td>
            <td>&nbsp;:&nbsp;</td>
            <td><b>{{userInfo.patronymic}}</b></td>
          </tr>
          <tr>
            <td>Phone type</td>
            <td>&nbsp;:&nbsp;</td>
            <td><b>{{userInfo.phoneType}}</b></td>
          </tr>

          </tbody>
        </table>

      </div>
      </div>
      <div *ngIf="mockRequest">
          {{mockRequest}}
      </div>
  
  
  `,
})
export class MainFormComponent {
  @Output() exit = new EventEmitter<void>();

  userInfo: UserInfo | null = null;
  loadUserInfoButtonEnabled: boolean = true;
  loadUserInfoError: string | null;
  mockRequest: string | null = null;

  constructor(private httpService: HttpService) {}

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
  //table/create-test
  loadMockRequestClicked() {
    // 'id':'1'
    // this.httpService.get( "/table/get-table-data",{'skip':0,'limit':6,'sortDirection':'ascending','sortType':'MINBALANCE'}).toPromise().then( result => {
    //   this.mockRequest=result.json().data;
    //   console.log(this.mockRequest);
    // });
    let myjson="{\n" +
        "      \"id\":\"4\",\n" +
        "      \"surname\":\"oo1\",\n" +
        "      \"name\":\"oo\",\n" +
        "      \"birthDate\":123456,\n" +
        "      \"charm\":\"BOI\",\n" +
        "      \"phones\":[\n" +
        "        {\n" +
        "          \"number\":\"123456789\",\n" +
        "          \"phoneType\":\"HOME\"\n" +
        "        },\n" +
        "        {\n" +
        "          \"number\":\"123456789\",\n" +
        "          \"phoneType\":\"MOBILE\"\n" +
        "        }\n" +
        "      ],\n" +
        "      \"factualAddress\":{\n" +
        "        \"flat\":\"12\",\n" +
        "        \"house\":\"12\",\n" +
        "        \"street\":\"12\",\n" +
        "        \"addressType\":\"FACT\"\n" +
        "      },\n" +
        "      \"registeredAddress\":{\n" +
        "        \"flat\":\"12\",\n" +
        "        \"house\":\"12\",\n" +
        "        \"street\":\"12\",\n" +
        "        \"addressType\":\"REG\"\n" +
        "      }\n" +
        "    }";

    let factAddress = new Address();
    factAddress.street = "AkhanSeri";
    factAddress.flat = "1324";
    factAddress.house = "213";
    factAddress.addressType = AddressType.FACT;
    let regAddress = new Address();
    regAddress.street = "AkhanSeri";
    regAddress.flat = "1324";
    regAddress.house = "213";
    regAddress.addressType = AddressType.REG;

    let user = User.copy({
        id: "5",
        surname: "Kali",
        name: "Oesultan",
        patronymic: "Amanzholuly",
        birthDate: 8645134,
        charm: CharmType.BATBOI,
        phones: [new Phone("+7777446", PhoneType.MOBILE)],
        factualAddress: factAddress,
        registeredAddress: regAddress,
    });


      // this.httpService.post( "/table/create-user", {
      //     user: JSON.stringify(user)
      // }).toPromise().then( result => {
      //     this.mockRequest=result.json();
      //     console.log(this.mockRequest);
      // });

      this.httpService.post( "/table/change-user", {
        user: JSON.stringify(user)
      }).toPromise().then( result => {
          this.mockRequest=result.json();
          console.log(this.mockRequest);
      });
      // this.httpService.post( "/table/delete-user", {'userID':'6'}).toPromise().then( result => {
      //     this.mockRequest=result.json();
      //     console.log(this.mockRequest);
      // });



      // this.httpService
  }
}
