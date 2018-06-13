import {AfterViewInit, Component, EventEmitter, Output} from "@angular/core";
import {HttpService} from "../HttpService";
import "rxjs/add/operator/catch";
import "rxjs/add/operator/map";
import "rxjs/add/operator/toPromise";
import {ClientDetails} from "../../model/ClientDetails";
import {Address} from "../../model/Address";
import {Phone} from "../../model/Phone";
import {Character} from "../../model/Character";
import {ClientToSave} from "../../model/ClientToSave";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'edit-form-component',
  template: require('./client-edit-form.component.html'),
  styles: [require('./client-edit-form.component.css')],
})
export class ClientEditFormComponent implements AfterViewInit {

  retrievedClient: ClientDetails;
  welcomeText = "Add new Client";
  clientInputForm: FormGroup;
  characters: Character[] = [];

  factAddress: Address = new Address();
  regAddress: Address = new Address();

  homePhone: Phone = new Phone();
  workingPhone: Phone = new Phone();
  mobilePhone1: Phone = new Phone();
  toSave = new ClientToSave();

  phoneMask: any[] = ['+', '7', ' ', '(', /[1-9]/, /\d/, /\d/, ')', ' ', /\d/, /\d/, /\d/, '-', /\d/, /\d/, /\d/, /\d/];

  public clientId: string = null;

  @Output() onClose = new EventEmitter<void>();
  @Output() returnChanges = new EventEmitter<any>();

  constructor(private httpService: HttpService) {
    this.clientInputForm = new FormBuilder().group({
      "surname": new FormControl("", [Validators.required, Validators.pattern("[a-zA-Z_]*")]),
      "name": new FormControl("", [Validators.required, Validators.pattern("[a-zA-Z_]*")]),
      "patronymic": new FormControl("", [Validators.required, Validators.pattern("[a-zA-Z_]*")]),
      "dateBirth": new FormControl("", Validators.required),
      "character": new FormControl("", Validators.required),
      "gender": new FormControl(0, Validators.required),
      "factStreet": new FormControl(),
      "factHouse": new FormControl(),
      "factFlat": new FormControl(),
      "regStreet": new FormControl("", Validators.required),
      "regHouse": new FormControl("", Validators.required),
      "regFlat": new FormControl("", Validators.required),
      "homePhone": new FormControl(),
      "workingPhone": new FormControl(),
      "mobilePhone": new FormControl("+7", Validators.required),
    });
    httpService.get("/client/characters").toPromise().then(result => {
      for (let chars of result.json()) {
        console.log(chars.name);
        this.characters.push(chars as Character)
      }
    }, error => {
      alert(error)
    })
  }

  omit_special_char(event) {
    let k;
    k = event.charCode;
    return ((k > 48 && k < 58));
  }

  closeModalForm() {
    this.clientId = null;
    this.nullifyToSave();
    this.onClose.emit();
    this.welcomeText = "Add new Client";
  }

  saveClient() {
    console.log(this.clientInputForm.getRawValue())
    if (this.clientInputForm.invalid) {
      alert("Fill all required fields");
      return;
    }

    this.homePhone.number = this.homePhone.number.replace(/\D/g, '');
    this.mobilePhone1.number = this.mobilePhone1.number.replace(/\D/g, '');
    this.workingPhone.number = this.workingPhone.number.replace(/\D/g, '');

    if (this.clientId == null) {

      this.toSave.addedAddresses.push(this.regAddress);

      if (this.factAddress.street.length != 0 && this.factAddress.house.length != 0
        && this.factAddress.flat.length != 0) {
        this.toSave.addedAddresses.push(this.factAddress);
      }

      this.toSave.addedPhones.push(this.mobilePhone1);

      if (this.clientInputForm.controls['homePhone'].valid) {
        this.toSave.addedPhones.push(this.homePhone);
      }

      if (this.clientInputForm.controls['workingPhone'].valid) {
        this.toSave.addedPhones.push(this.workingPhone);
      }

    } else {
      let factNum = -1;
      let regNum = 0;
      let homeNum = -1;
      let workingNum = -1;
      let mobilePhoneNum = -1;

      this.retrievedClient.phones.forEach((val, index) => {
        if (val.type == "MOBILE") {
          mobilePhoneNum = index;
        }
        if (val.type == "HOME") {
          homeNum = index;
        }
        if (val.type == "WORKING") {
          workingNum = index;
        }
      });
      this.retrievedClient.addresses.forEach((val, index) => {
        if (val.type == "FACT") {
          factNum = index;
        } else {
          regNum = index;
        }
      });

      if (this.factAddress.house.length != 0
        && this.factAddress.street.length != 0
        && this.factAddress.flat.length != 0) {
        if (factNum != -1) {
          if (this.addressesNotEqual(this.retrievedClient.addresses[factNum], this.factAddress)) {
            this.toSave.editedAddresses.push(this.factAddress);
          }
        } else {
          this.toSave.addedAddresses.push(this.factAddress);
        }
      } else {
        if (factNum != -1) {
          this.toSave.deletedAddresses.push(this.retrievedClient.addresses[factNum]);
        }
      }
      if (this.addressesNotEqual(this.retrievedClient.addresses[regNum], this.regAddress)) {
        this.toSave.editedAddresses.push(this.regAddress);
      }

      this.validateAndPutPhone(this.homePhone,
        this.clientInputForm.controls['homePhone'].valid,
        homeNum);

      this.validateAndPutPhone(this.workingPhone,
        this.clientInputForm.controls['workingPhone'].valid,
        workingNum);
      console.log("Changed:" + this.mobilePhone1.number + " " + this.retrievedClient.phones[mobilePhoneNum].number)
      this.validateAndPutPhone(this.mobilePhone1,
        true,
        mobilePhoneNum);
    }
    this.httpService.post("/client/save", {editedClient: JSON.stringify(this.toSave)}).toPromise().then(result => {
      this.returnChanges.emit(result.json());
      this.closeModalForm();
    }, error => {
      alert(error);
    });
  }

  validateAndPutPhone(phone1: Phone, valid: boolean, phoneNum: number) {
    if (phoneNum != -1) {
      if (phone1.number.length != 0) {
        if (phone1.number
          != this.retrievedClient.phones[phoneNum].number
          && valid) {
          let number = phone1.number;
          phone1.number = this.retrievedClient.phones[phoneNum].number;
          phone1.editedTo = number;
          this.toSave.editedPhones.push(phone1);
        }
      } else {
        this.toSave.deletedPhones.push(this.retrievedClient.phones[phoneNum]);
      }
    } else {
      if (this.homePhone.number != null) {
        this.toSave.addedPhones.push(phone1);
      }
    }
  }

  addressesNotEqual(add1: Address, add2: Address): boolean {
    return add1.street != add2.street
      || add1.house != add2.house
      || add1.flat != add2.flat;

  }

  public loadFromDatabase(clientId) {
    this.clientId = clientId;
    this.welcomeText = "Edit client";
    this.httpService.get("/client/details",
      {clientId: this.clientId}).toPromise().then(result => {
      this.retrievedClient = result.json();

      this.toSave.id = this.retrievedClient.id;
      this.toSave.surname = this.retrievedClient.surname;
      this.toSave.name = this.retrievedClient.name;
      this.toSave.surname = this.retrievedClient.surname;
      this.toSave.patronymic = this.retrievedClient.patronymic;
      this.toSave.charm = this.characters[this.retrievedClient.charm].id;
      this.toSave.gender = this.retrievedClient.gender;
      this.toSave.birthDate = this.retrievedClient.birthDate;

      for (let phone of this.retrievedClient.phones) {
        if (phone.type == "HOME") {
          this.homePhone = Phone.createNewPhone(phone);
        } else if (phone.type == "WORKING") {
          this.workingPhone = Phone.createNewPhone(phone);
        } else if (phone.type == "MOBILE") {
          this.mobilePhone1 = Phone.createNewPhone(phone);
        }
      }

      for (let address of this.retrievedClient.addresses) {
        if (address.type == "FACT") {
          this.factAddress = Address.createNewAddress(address);
        } else if (address.type == "REG") {
          this.regAddress = Address.createNewAddress(address);
        }
      }
    }, error => {
      alert("Error from retrieving " + error)
    });
  }

  ngAfterViewInit(): void {
    this.nullifyToSave();
  }

  nullifyToSave() {
    this.toSave = new ClientToSave();
    this.regAddress = new Address();
    this.factAddress = new Address();
    this.homePhone = new Phone();
    this.workingPhone = new Phone();
    this.mobilePhone1 = new Phone();
    this.regAddress.type = "REG";
    this.factAddress.type = "FACT";

    this.homePhone.type = "HOME";
    this.workingPhone.type = "WORKING";
    this.mobilePhone1.type = "MOBILE";
  }
}