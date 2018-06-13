import {Component, Input, OnInit, OnChanges, SimpleChanges, Inject} from '@angular/core';
import { User } from "../../../models/User";
import { HttpService } from "../../../services/HttpService";
import {CharmType} from "../../../models/CharmType";
import {Address} from "../../../models/Address";
import {PhoneType} from "../../../models/PhoneType";
import {Phone} from "../../../models/Phone";
import {AddressType} from "../../../models/AddressType";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-user-dialog',
  templateUrl: './user-dialog.component.html',
  styleUrls: ['./user-dialog.component.css']
})
export class UserDialogComponent implements OnInit {
  form: FormGroup;
  userID: string;
  titleType: string;



  // @Input() selectedUserID: String;
  //
  // newUser = new User();
  // newUser.id='-1';
  // newUser.name='';
  // newUser.surname='';
  // newUser.patronymic='';
  // newUser.charm=CharmType.BOI;
  // newUser.birthDate=0;
  // let newRegAddress = new Address();
  // let newFacAddress = new Address();
  // newRegAddress.addressType=AddressType.REG;
  // newFacAddress.addressType=AddressType.FACT;
  // newRegAddress.flat="";
  // newRegAddress.house="";
  // newRegAddress.street="";
  // newFacAddress.flat="";
  // newFacAddress.house="";
  // newFacAddress.street="";
  // let phone = new Phone("",PhoneType.MOBILE);
  // newUser.phones=[phone];
  // newUser.registeredAddress=newRegAddress;
  // newUser.factualAddress=newFacAddress;




  // ngOnChanges(changes: SimpleChanges){
  //   for (let propName in changes){
  //     let change = changes[propName];
  //     let cur  = JSON.stringify(change.currentValue);
  //     let prev  = JSON.stringify(change.previousValue);
  //
  //   }

  save():void{

  }
  close():void{
    this.dialogRef.close();
  }



  constructor(
    private httpService:HttpService,
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<UserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) data
  ) {
    this.titleType = data.titleType;
    this.userID = data.userID;
  }
  // httpService.get
  ngOnInit() {

  }

}
