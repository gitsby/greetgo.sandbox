import { Component, EventEmitter, Output } from "@angular/core";
import { UserInfo } from "../../models/UserInfo";
import { HttpService } from "../../services/HttpService";
import { PhoneType } from "../../models/PhoneType";
import { TableModel } from "../../models/TableModel";
import { User } from "../../models/User";
import { Phone } from "../../models/Phone";
import { Address } from "../../models/Address";
import { CharmType } from "../../models/CharmType";
import { AddressType } from "../../models/AddressType";
import { MatDialogRef,MatDialog, MatDialogConfig } from '@angular/material';
import { UserDialogComponent } from './user-dialog/user-dialog.component';
import { Observable } from 'rxjs/';
// import 'rxjs/add/observable/fromPromise';
import {map} from "rxjs/operators";

@Component({
  selector: 'main-form-component',
  templateUrl: './main-form.component.html',
})
export class MainFormComponent {
  @Output() exit = new EventEmitter<void>();

  userInfo: UserInfo | null = null;
  loadUserInfoButtonEnabled: boolean = true;
  loadUserInfoError: string | null;
  mockRequest: string | null = null;
  selectedUserID: string | null = null;
  typeOfDialogCall: string | null = null;
  userDialogRef: MatDialogRef<UserDialogComponent>;
  constructor(private httpService: HttpService, private dialog: MatDialog) {}

  openDialog(userID:string, titleType:string){

    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.hasBackdrop=true;

    dialogConfig.data = {
      selectedUserId: userID,
      typeOfDialogCall: titleType
    };
    this.dialog.open(UserDialogComponent, dialogConfig);


  }

  deleteButtonClicked():void{
    console.log("deleteButton triggered");
    this.httpService.post("/table/delete-user",{"userID":this.selectedUserID}).toPromise().then( result => {
      console.log(result.json());
      alert("User was deleted");
     });
  }




  updateButtonClicked():void{
    console.log("updateButton triggered");
    this.openDialog(this.selectedUserID,"Update");
    // this.userDialogRef = this.dialog.open(UserDialogComponent, {hasBackdrop:false});

    // let user = new User();
    // let userJson = "";
    //
    // this.httpService.get("/table/get-exact-user",{"userID":this.selectedUserID}).toPromise().then( result => {
    //   userJson = result.json();
    //   user.assign(userJson);
    //   // dialogue
    // });

  }
  createButtonClicked():void{
    console.log("createButton triggered");
    this.openDialog('-1',"Create");
    // this.userDialogRef = this.dialog.open(UserDialogComponent, {hasBackdrop:false});
  }



  // loadUserInfoButtonClicked() {
  //   this.loadUserInfoButtonEnabled = false;
  //   this.loadUserInfoError = null;
  //
  //   this.httpService.get("/auth/userInfo").toPromise().then(result => {
  //     this.userInfo = UserInfo.copy(result.json());
  //     let phoneType: PhoneType | null = this.userInfo.phoneType;
  //     console.log(phoneType);
  //   }, error => {
  //     console.log(error);
  //     this.loadUserInfoButtonEnabled = true;
  //     this.loadUserInfoError = error;
  //     this.userInfo = null;
  //   });
  // }
  //table/create-test
  // loadMockRequestClicked() {
  //
  //   // 'id':'1'
  //   this.httpService.get( "/table/get-table-data",{'skip':0,'limit':6,'sortDirection':'ascending','sortType':'MINBALANCE'}).toPromise().then( result => {
  //     this.mockRequest=result.json().data;
  //
  //   });
  //
  //
  //   // let factAddress = new Address();
  //   // factAddress.street = "AkhanSeri";
  //   // factAddress.flat = "1324";
  //   // factAddress.house = "213";
  //   // factAddress.addressType = AddressType.FACT;
  //   // let regAddress = new Address();
  //   // regAddress.street = "AkhanSeri";
  //   // regAddress.flat = "1324";
  //   // regAddress.house = "213";
  //   // regAddress.addressType = AddressType.REG;
  //   //
  //   // let user = User.copy({
  //   //     id: "5",
  //   //     surname: "Kali",
  //   //     name: "Oesultan",
  //   //     patronymic: "Amanzholuly",
  //   //     birthDate: 8645134,
  //   //     charm: CharmType.BATBOI,
  //   //     phones: [new Phone("+7777446", PhoneType.MOBILE)],
  //   //     factualAddress: factAddress,
  //   //     registeredAddress: regAddress,
  //   // });
  //
  //
  //     // this.httpService.post( "/table/create-user", {
  //     //     user: JSON.stringify(user)
  //     // }).toPromise().then( result => {
  //     //     this.mockRequest=result.json();
  //     //     console.log(this.mockRequest);
  //     // });
  //
  //     // this.httpService.post( "/table/change-user", {
  //     //   user: JSON.stringify(user)
  //     // }).toPromise().then( result => {
  //     //     this.mockRequest=result.json();
  //     //     console.log(this.mockRequest);
  //     // });
  //     // this.httpService.post( "/table/delete-user", {'userID':'6'}).toPromise().then( result => {
  //     //     this.mockRequest=result.json();
  //     //     console.log(this.mockRequest);
  //     // });
  //     // this.httpService.get( "/table/get-exact-user", {'userID':'5'}).toPromise().then( result => {
  //     //     this.mockRequest=result.json();
  //     //     console.log(this.mockRequest);
  //     // });
  //
  //
  //
  //   // this.httpService
  // }
}
