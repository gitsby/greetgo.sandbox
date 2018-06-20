import {Component, Input, OnInit, OnChanges, SimpleChanges, Inject} from '@angular/core';
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


  constructor(
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<UserDialogComponent>,
    private httpService: HttpService,
    @Inject(MAT_DIALOG_DATA) private data,
    private picker: MatDatepickerModule,
  ) {
  }

  ngOnInit() {
    console.log(this.charms);
    console.log(this.data.user);
    const id = this.data.user.id;
    const name = this.data.user.name;
    const surname = this.data.user.surname;
    const patronymic = this.data.user.patronymic;
    const charm = this.data.user.charm;
    const birthDate = new Date(this.data.user.birthDate * 1000);
    const titleType = this.data.user.titleType;
    let phones = this.data.user.phones;
    const factualAddress = this.data.user.factualAddress;
    const registeredAddress = this.data.user.registeredAddress;
    const genderType = this.data.user.genderType;
    console.log(phones);

    this.form = this.formBuilder.group({
      id: id,
      name: [name, Validators.required],
      surname: [surname, Validators.required],
      patronymic: [patronymic, Validators.required],
      charm: [charm, Validators.required],
      birthDate: [new Date(birthDate/1000), Validators.required],
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

  public submit(form) {
    let user = User.copy(this.form.getRawValue());
    user.birthDate = this.form.getRawValue().birthDate.getTime();
    if (this.data.titleType === "Update")
      this.httpService.post('/table/change-user', {
        user: JSON.stringify(user),
      }).toPromise().then(
        res => {
          alert(res.text());
          this.dialogRef.close(`${form.value.filename}`);
        }
      );
    else
      this.httpService.post('/table/create-user', {
        user: JSON.stringify(user),
      }).toPromise().then(
        res => {
          alert(res.text());
          this.dialogRef.close(`${form.value.filename}`);
        }
      );
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
