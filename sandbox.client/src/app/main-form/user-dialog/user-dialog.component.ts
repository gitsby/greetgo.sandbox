import {Component, Input, OnInit, OnChanges, SimpleChanges, Inject, ViewChild} from '@angular/core';
import { User } from "../../../models/User";
import { HttpService } from "../../../services/HttpService";
import {CharmType} from "../../../models/CharmType";
import {Address} from "../../../models/Address";
import {PhoneType} from "../../../models/PhoneType";
import {Phone} from "../../../models/Phone";
import {AddressType} from "../../../models/AddressType";
import {MAT_DIALOG_DATA, MatDatepickerModule, MatDialogRef} from "@angular/material";
import {
  FormBuilder,
  FormGroup,
  FormArray,
  Validators,
  ValidatorFn,
  AbstractControl,
  ValidationErrors
} from "@angular/forms";
import {MatDatepickerInputEvent} from '@angular/material/datepicker';
import {GenderType} from "../../../models/GenderType";
import {UsersTableCustomDatasource} from "../users-table/users-table-custom-datasource";
import {UsersTableComponent} from "../users-table/users-table.component";

@Component({
  selector: 'app-user-dialog',
  templateUrl: './user-dialog.component.html',
  styleUrls: ['./user-dialog.component.css']
})
export class UserDialogComponent implements OnInit {

  form: FormGroup;
  charms = Object.keys(CharmType);
  phoneTypes = Object.keys(PhoneType);
  genderTypes = Object.keys(GenderType);
  gettingOrSendingDataToServer:boolean=true;
  user:User;
  doWeHaveDataOrNot: boolean=false;

  constructor(
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<UserDialogComponent>,
    private httpService: HttpService,
    @Inject(MAT_DIALOG_DATA) private data,
    private picker: MatDatepickerModule,
  ) {}

  createForm(user:User){
    console.log("\nGONNNNA CREAATTE YOUUUR FUUURRRMM\n");
    this.gettingOrSendingDataToServer=false;
    this.doWeHaveDataOrNot=true;
    const id = user.id;
    const name = user.name;
    const surname = user.surname;
    const patronymic = user.patronymic;
    const charm = user.charm;
    const birthDate = new Date(user.birthDate);
    let phones = user.phones;
    const factualAddress = user.factualAddress;
    const registeredAddress = user.registeredAddress;
    const genderType = user.genderType;
    this.form = this.formBuilder.group({
      id: id,
      name: [name, Validators.required],
      surname: [surname, Validators.required],
      patronymic: [patronymic],
      charm: [charm, Validators.required],
      birthDate: [birthDate, Validators.required],
      genderType: [genderType, Validators.required],
      registeredAddress: this.formBuilder.group({
        street: [registeredAddress.street, Validators.required],
        house: [registeredAddress.house, Validators.required],
        flat: [registeredAddress.flat, Validators.required],
      }),
      factualAddress: this.formBuilder.group({
        street: factualAddress.street,
        house: factualAddress.house,
        flat: factualAddress.flat,
      }),
      phones: this.formBuilder.array(phones.map((userPhone) => this.phoneGroup(userPhone.number, userPhone.phoneType))),
    });
    this.form.controls['phones'].setValidators(mobileExistenceValidator());
  };

  ngOnInit() {
    this.gettingOrSendingDataToServer=true;
    console.log("data id ngoninit" + this.data.id);
    this.data.id===null?this.generateNewUser():this.getSelectedUser(this.data.id);

  }

  get phonesFormArray(): FormArray {
    return this.form.get('phones') as FormArray;
  }

  public addNumber() {
    this.phonesFormArray.push(this.phoneGroup(''));
  }

  private phoneGroup(number: string, phoneType: PhoneType = PhoneType.MOBILE) {
    return this.formBuilder.group({
      phoneType: this.formBuilder.control(phoneType),
      number: this.formBuilder.control(number.toString(), [
        checkNum()
      ]),
    });
  }

  public deleteNumber(elIndex) {
    this.phonesFormArray.removeAt(elIndex);
  }

  public submit() {

    this.gettingOrSendingDataToServer=true;
    let user = User.copy(this.form.getRawValue());
    user.birthDate = this.form.getRawValue().birthDate.getTime();
    this.user=user;
    if (this.data.id !== null) {
      this.httpService.post('/table/change-user', {
        user: JSON.stringify(this.user),
      }).toPromise().then(
        () => {
          this.gettingOrSendingDataToServer = false;
          console.log( "submit "+ user);
          this.doWeHaveDataOrNot=false;
          this.dialogRef.close({user:this.user,state:true});
        }
      );
    }
    else{
      this.httpService.post('/table/create-user', {
        user: JSON.stringify(this.user),
      }).toPromise().then(
        (res) => {
          this.gettingOrSendingDataToServer=false;
          let id=res.json();

          console.log(id);
          console.log(typeof id);
          this.user.id=id.toString();
          console.log(this.user.id);
          this.doWeHaveDataOrNot=false;
          this.dialogRef.close({user:this.user,state:true});
        }
      );
    }
  }
  getSelectedUser(id:string):void{
    this.httpService.get('/table/get-exact-user', {'userID': id}).subscribe(res=>{
      console.log(res);
      this.doWeHaveDataOrNot=false;
      this.createForm(User.copy(res.json()))});
  }


  generateNewUser():void{
    let user = new User();
    user.phones = [new Phone('', PhoneType.MOBILE)];
    user.name = "";
    user.surname = "";
    user.patronymic = "";
    user.charm = CharmType.BOI;
    user.birthDate = 0;
    user.factualAddress = new Address();
    user.registeredAddress = new Address();
    user.id = '-1';
    this.createForm(user);
  }


  closeButton() {
    this.gettingOrSendingDataToServer=false;
    this.dialogRef.close({user:null,state:false});
  }
}
export function mobileExistenceValidator():ValidatorFn {

  return (array: AbstractControl): ValidationErrors|null=>{
    let noMobile=false;
    for(let el of array.value){
      noMobile=noMobile||el.phoneType==PhoneType.MOBILE;
    }
    return noMobile?null:{mobileExists:true};
  };
}

export function checkNum():ValidatorFn {
  return (control: AbstractControl): {[key: string]: any} | null=>{
    return (control.value.toString()).match("^(\\d{11})?$")? null: {value: control.value}
  }
}
