import {Component, EventEmitter, Output, ViewChild} from "@angular/core";
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
import {UsersTableComponent} from "./users-table/users-table.component";

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
  selectedUserID: string = '0';
  selectedUser:User = this.generateNewUser();
  isThereData:boolean=true;
  typeOfDialogCall: string | null = null;
  userDialogRef: MatDialogRef<UserDialogComponent>;
  @ViewChild(UsersTableComponent) private usersTableComponent:UsersTableComponent;

  constructor(private httpService: HttpService, private dialog: MatDialog) {}


  selectedUserIDChange(changedUser){
    this.isThereData=true;
    this.selectedUserID = changedUser;
    this.selectedUser=User.copy(this.getSelectedUser());
    console.log("damndisguysissolet",this.selectedUser);
    console.log(this.selectedUserID);
  }

  openDialog(user:User, titleType:string){
    this.dialog.open(UserDialogComponent, {
      hasBackdrop: true,
      minWidth:400,
      data: {
        user: user,
        titleType: titleType
      }
    });
    this.dialog.afterAllClosed.subscribe(()=>this.usersTableComponent.loadTablePage());
  }


  deleteButtonClicked():void{
    console.log("deleteButton triggered");
    this.httpService.post("/table/delete-user",{"userID":this.selectedUserID}).toPromise().then( result => {
      console.log(result.json());
      alert("User was deleted");
      this.usersTableComponent.loadTablePage();
     });

  }
  updateButtonClicked():void{
    console.log("updateButton triggered");
    this.openDialog(this.selectedUser,"Update");

  }
  createButtonClicked():void{
    console.log("createButton triggered");
    this.openDialog(this.generateNewUser() ,"Create");
  }

  getSelectedUser(){
    console.log(this.selectedUserID);
    let user = new User();
     return (this.httpService.get('/table/get-exact-user',{'userID':this.selectedUserID}).toPromise().then(
       res=>{
         console.log(res.json());
         this.selectedUser=User.copy(res.json());
         this.isThereData=false;
         return res.json()}
     ));
  }


  generateNewUser():User{
    let user = new User();
    user.phones=[new Phone('',PhoneType.MOBILE)];
    user.name="";
    user.surname="";
    user.patronymic="";
    user.charm=CharmType.BOI;
    user.birthDate=0;
    user.factualAddress=new Address();
    user.registeredAddress=new Address();
    user.id='-1';
    return user
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
