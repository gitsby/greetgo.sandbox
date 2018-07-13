import {Component, Input, OnInit, OnChanges, SimpleChanges, Inject, ViewChild} from '@angular/core';
import { Client } from "../../../models/Client";
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
import {ClientRecordsCustomDatasource} from "../client-records/client-records-custom-datasource";
import {ClientRecordsComponent} from "../client-records/client-records.component";

@Component({
  selector: 'app-client-dialog',
  templateUrl: './client-dialog.component.html',
  styleUrls: ['./client-dialog.component.css']
})
export class ClientDialogComponent implements OnInit {

  form: FormGroup;
  charms = Object.keys(CharmType);
  phoneTypes = Object.keys(PhoneType);
  genderTypes = Object.keys(GenderType);
  gettingOrSendingDataToServer:boolean=true;
  client:Client;
  doWeHaveDataOrNot: boolean=false;

  constructor(
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<ClientDialogComponent>,
    private httpService: HttpService,
    @Inject(MAT_DIALOG_DATA) private data,
    private picker: MatDatepickerModule,
  ) {}

  createForm(client:Client){
    console.log("\nGONNNNA CREAATTE YOUUUR FUUURRRMM\n");
    this.gettingOrSendingDataToServer=false;
    this.doWeHaveDataOrNot=true;
    const id = client.id;
    const name = client.name;
    const surname = client.surname;
    const patronymic = client.patronymic;
    const charm = client.charm;
    const birthDate = new Date(client.birthDate);
    let phones = client.phones;
    const factualAddress = client.factualAddress;
    const registeredAddress = client.registeredAddress;
    const genderType = client.genderType;
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
      phones: this.formBuilder.array(phones.map((clientPhone) => this.phoneGroup(clientPhone.number, clientPhone.phoneType))),
    });
    this.form.controls['phones'].setValidators(mobileExistenceValidator());
  };

  ngOnInit() {
    this.gettingOrSendingDataToServer=true;
    console.log("data id ngoninit" + this.data.id);
    this.data.id===null?this.generateNewClient():this.getSelectedClientFromServer(this.data.id);

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
    let client = Client.copy(this.form.getRawValue());
    client.birthDate = this.form.getRawValue().birthDate.getTime();
    this.client=client;
    if (this.data.id !== null) {
      this.httpService.post('/client-records/change-client', {
        client: JSON.stringify(this.client),
      }).toPromise().then(
        () => {
          this.gettingOrSendingDataToServer = false;
          console.log( "submit "+ client);
          this.doWeHaveDataOrNot=false;
          this.dialogRef.close({client:this.client,state:true});
        }
      );
    }
    else{
      this.httpService.post('/client-records/create-client', {
        client: JSON.stringify(this.client),
      }).toPromise().then(
        (res) => {
          this.gettingOrSendingDataToServer=false;
          let id=res.json();

          console.log(id);
          console.log(typeof id);
          this.client.id=id.toString();
          console.log(this.client.id);
          this.doWeHaveDataOrNot=false;
          this.dialogRef.close({client:this.client,state:true});
        }
      );
    }
  }

  // TODO: по наименованию не понятно, что этот метод делает на самом деле.
  // Кажется, что он должен вывести мне только пользователя и всё.
  // TODO: назови правильно.

  getSelectedClientFromServer(id:string):void{
    this.httpService.get('/client-records/get-exact-client', {'clientId': id}).subscribe(res=>{
      console.log(res);
      this.doWeHaveDataOrNot=false;
      this.createForm(Client.copy(res.json()))});
  }


  generateNewClient():void{
    let client = new Client();
    client.phones = [new Phone('', PhoneType.MOBILE)];
    client.name = "";
    client.surname = "";
    client.patronymic = "";
    client.charm = CharmType.BOI;
    client.birthDate = 0;
    client.factualAddress = new Address();
    client.registeredAddress = new Address();
    client.id = '-1';
    this.createForm(client);
  }


  closeButton() {
    this.gettingOrSendingDataToServer=false;
    this.dialogRef.close({client:null,state:false});
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
