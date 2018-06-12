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
import {ClientInput} from "../../model/ClientInput";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";

let retrievedClient: ClientDetails;

@Component({
  selector: 'edit-form-component',
  template: require('./edit_form.component.html'),
  styles: [require('./edit_form.component.css')],
})
export class EditFormComponent implements AfterViewInit {

  welcomeText = "Add new Client";
  clientInputForm: FormGroup;
  characters: Character[] = []
  clientInput: ClientInput = new ClientInput();
  factAddress: Address = new Address();
  regAddress: Address = new Address();
  homePhone: Phone = new Phone();
  workingPhone: Phone = new Phone();
  mobilePhone1: Phone = new Phone();

  phoneMask: any[] = ['+', '7', ' ', '(', /[1-9]/, /\d/, /\d/, ')', ' ', /\d/, /\d/, /\d/, '-', /\d/, /\d/, /\d/, /\d/];

  public clientId: string = null;

  mobilePhonesInput = [];


  @Output() onClose = new EventEmitter<void>();
  @Output() returnChanges = new EventEmitter<any>();

  constructor(private httpService: HttpService) {
    this.clientInputForm = new FormBuilder().group({
      "surname": new FormControl("", [Validators.required, Validators.pattern("[a-zA-Z_]*")]),
      "name": new FormControl("", [Validators.required, Validators.pattern("[a-zA-Z_]*")]),
      "patronymic": new FormControl("", [Validators.required, Validators.pattern("[a-zA-Z_]*")]),
      "dateBirth": new FormControl("", Validators.required),
      "character": new FormControl("", Validators.required),
      "gender": new FormControl("", Validators.required),
      "factStreet": new FormControl(),
      "factHouse": new FormControl(),
      "factFlat": new FormControl(),
      "regStreet": new FormControl("", Validators.required),
      "regHouse": new FormControl("", Validators.required),
      "regFlat": new FormControl("", Validators.required),
      "homePhone": new FormControl("+7", Validators.required),
      "workingPhone": new FormControl("+7", Validators.required),
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
    focus()
    var k;
    k = event.charCode;  //         k = event.keyCode;  (Both can be used)
    return ((k > 48 && k < 58));
  }

  closeModalForm() {
    this.clientId = null;
    this.initElements();
    this.onClose.emit();
    // document.getElementById("myModal").style.display = "none";
    this.welcomeText = "Add new Client";
  }

  saveClient() {
    if (this.clientInputForm.controls['surname'].invalid || this.clientInputForm.controls['name'].invalid ||
      this.clientInputForm.controls['patronymic'].invalid ||
      this.clientInputForm.controls['dateBirth'].invalid || (this.clientInput.character == null || this.clientInput.character == -1) ||
      this.clientInputForm.controls['regStreet'].invalid || this.clientInputForm.controls['regHouse'].invalid ||
      this.clientInputForm.controls['regFlat'].invalid || this.mobilePhonesEmpty() || this.clientInput.gender == undefined) {
      alert("Fill all required fields");
      return;
    }
    this.homePhone.number = this.homePhone.number.replace(/\D/g, '');
    this.mobilePhone1.number = this.mobilePhone1.number.replace(/\D/g, '');
    this.workingPhone.number = this.workingPhone.number.replace(/\D/g, '');

    let editClient: ClientToSave = new ClientToSave();
    if (this.clientId == null) { // When adding
      editClient.id = null;
      editClient.name = this.clientInput.name;
      editClient.surname = this.clientInput.surname;
      editClient.patronymic = this.clientInput.patronymic;
      editClient.charm = this.characters[this.clientInput.character].id;
      editClient.gender = this.clientInput.gender;
      editClient.birthDate = new Date(this.clientInput.birthDate);

      let addresses: Address[] = [];
      if (this.regAddress.flat.length != 0 && this.regAddress.house.length != 0
        && this.regAddress.flat.length != 0) {
        addresses.push(this.regAddress);
      } else {
        alert("Fill registration address");
        return;
      }
      if (this.factAddress.street.length != 0 && this.factAddress.house.length != 0
        && this.factAddress.flat.length != 0) {
        this.factAddress.type = "FACT";
        addresses.push(this.factAddress);
      }

      let phones: Phone[] = [];

      if (!this.clientInputForm.controls['homePhone'].invalid) {
        this.homePhone.type = "HOME";
        phones.push(this.homePhone);
      }

      if (!this.clientInputForm.controls['workingPhone'].invalid) {
        this.workingPhone.type = "WORKING";
        phones.push(this.workingPhone);
      }
      if (!this.clientInputForm.controls['mobilePhone'].invalid) {
        this.mobilePhone1.type = "MOBILE";
        phones.push(this.workingPhone);
      }

      editClient.addedAddresses = addresses;
      editClient.addedPhones = phones;
      console.log("FINISHED ADDING");
      console.log(editClient.toString())
    } else {// When editing
      let factNum = -1;
      let regNum = 0;
      let homeNum = -1;
      let workingNum = -1;
      let mobilePhoneNum = -1;
      retrievedClient.phones.forEach((val, index) => {
        if (val.type == "MOBILE") {
          mobilePhoneNum = index;
        }
        if (val.type == "HOME") {
          homeNum = index;
        }
        if (val.type == "WORKING") {
          workingNum = index;
        }
        console.log("Received type::" + typeof val)
      });
      retrievedClient.addresses.forEach((val, index) => {
        if (val.type == "FACT") {
          factNum = index;
        } else {
          regNum = index;
        }
      });
      this.regAddress.clientId = retrievedClient.id;
      this.factAddress.clientId = retrievedClient.id;

      this.homePhone.client = retrievedClient.id;
      this.workingPhone.client = retrievedClient.id;
      this.mobilePhone1.client = retrievedClient.id;
      editClient.id = retrievedClient.id;

      if (retrievedClient.name != this.clientInput.name) {
        editClient.name = this.clientInput.name;
      }
      if (retrievedClient.surname != this.clientInput.surname) {
        editClient.surname = this.clientInput.surname;
      }
      if (retrievedClient.patronymic != this.clientInput.patronymic) {
        editClient.patronymic = this.clientInput.patronymic;
      }
      if (this.clientInput.birthDate != new Date(retrievedClient.birthDate).toISOString().split('T')[0]) {
        editClient.birthDate = new Date(this.clientInput.birthDate);
      }
      if (this.clientInput.gender != retrievedClient.gender) {
        editClient.gender = this.clientInput.gender;
      }
      if (this.clientInput.gender != retrievedClient.gender) {
        editClient.gender = this.clientInput.gender;
      }
      console.log(typeof retrievedClient.phones[mobilePhoneNum]);

      if (this.factAddress.house.length != 0
        && this.factAddress.street.length != 0
        && this.factAddress.flat.length != 0) {
        if (factNum != -1) {
          if (retrievedClient.addresses[factNum].street != this.factAddress.street
            || retrievedClient.addresses[factNum].house != this.factAddress.house
            || retrievedClient.addresses[factNum].flat != this.factAddress.flat) {
            editClient.editedAddresses.push(this.factAddress);
          }
        } else {
          editClient.addedAddresses.push(this.factAddress);
        }
      } else {
        if (factNum != -1) {
          editClient.deletedAddresses.push(retrievedClient.addresses[factNum]);
        }
      }
      if (this.regAddress.house.length != 0
        && this.regAddress.street.length != 0
        && this.regAddress.flat.length != 0) {
        if (retrievedClient.addresses[regNum].street != this.regAddress.street
          || retrievedClient.addresses[regNum].house != this.regAddress.house
          || retrievedClient.addresses[regNum].flat != this.regAddress.flat) {
          editClient.editedAddresses.push(this.regAddress);
        }
      } else {
        alert("Register Address must not be empty");
        return;
      }

      if (homeNum != -1) {
        if (this.homePhone.number.length != 0) {
          if (this.homePhone.number != retrievedClient.phones[homeNum].number
            && !this.clientInputForm.controls['homePhone'].invalid) {
            let number = this.homePhone.number;
            this.homePhone.number = retrievedClient.phones[homeNum].number;
            this.homePhone.editedTo = number;
            editClient.editedPhones.push(this.homePhone);
          }
        } else {
          editClient.deletedPhones.push(retrievedClient.phones[homeNum]);
        }
      } else {
        if (this.homePhone.number != null) {
          editClient.addedPhones.push(this.homePhone);
        }
      }

      if (workingNum != -1) {
        if (this.workingPhone.number.length != 0) {
          if (this.workingPhone.number
            != retrievedClient.phones[workingNum].number
            && !this.clientInputForm.controls['workingPhone'].invalid) {
            let number = this.workingPhone.number;
            this.workingPhone.number = retrievedClient.phones[workingNum].number;
            this.workingPhone.editedTo = number;
            editClient.editedPhones.push(this.workingPhone);
          }
        } else {
          editClient.deletedPhones.push(retrievedClient.phones[workingNum]);
        }
      } else {
        if (this.workingPhone.number != null) {
          editClient.addedPhones.push(this.workingPhone);
        }
      }

      if (mobilePhoneNum != -1) {
        if (this.mobilePhone1.number.length != 0) {

          if (this.mobilePhone1.number
            != retrievedClient.phones[mobilePhoneNum].number
            && !this.clientInputForm.controls['mobilePhone'].invalid) {
            let number = this.mobilePhone1.number;
            this.mobilePhone1.number = retrievedClient.phones[mobilePhoneNum].number;
            this.mobilePhone1.editedTo = number;

            editClient.editedPhones.push(this.mobilePhone1);
          }
        } else {
          editClient.deletedPhones.push(retrievedClient.phones[mobilePhoneNum]);
        }
      } else {
        if (this.mobilePhone1.number != null) {
          editClient.addedPhones.push(this.mobilePhone1);
        }
      }
    }
    console.log(editClient.toString())

    this.httpService.post("/client/save", {editedClient: editClient.toString()}).toPromise().then(result => {
      this.returnChanges.emit(result.json());
      alert(result.json())
      this.closeModalForm();
    }, error => {
      alert(error);
    });
  }

  public loadFromDatabase(clientId) {
    this.clientId = clientId;
    this.welcomeText = "Edit client";
    console.log("started .........");
    this.httpService.get("/client/getClientWithId",
      {clientId: this.clientId + ""}).toPromise().then(result => {
      retrievedClient = result.json();

      this.clientInput.name = retrievedClient.name;
      this.clientInput.surname = retrievedClient.surname;
      this.clientInput.patronymic = retrievedClient.patronymic;
      this.clientInput.character = this.characters[retrievedClient.charm].id;
      this.clientInput.gender = "MALE";
      this.clientInput.birthDate = new Date(retrievedClient.birthDate).toISOString().split('T')[0];

      let homePhone: Phone = null;
      let workingPhone: Phone = null;
      let mobilePhone: Phone = null;

      for (let phone of retrievedClient.phones) {
        if (phone.type == "HOME") {
          homePhone = phone;
        } else if (phone.type == "WORKING") {
          workingPhone = phone;
        } else if (phone.type == "MOBILE") {
          mobilePhone = phone;
        }
      }

      let factAddress: Address = null;
      let regAddress: Address = null;
      for (let address of retrievedClient.addresses) {
        if (address.type == "FACT") {
          console.log("Address " + address.id);
          factAddress = address;
        } else if (address.type == "REG") {
          console.log("Address " + address.id);
          regAddress = address;
        }
      }

      if (factAddress != null) {
        this.factAddress.id = factAddress.id;
        this.factAddress.flat = factAddress.flat;
        this.factAddress.house = factAddress.house;
        this.factAddress.street = factAddress.street;
      }

      if (regAddress != null) {
        this.regAddress.id = regAddress.id;
        this.regAddress.house = regAddress.house;
        this.regAddress.street = regAddress.street;
        this.regAddress.flat = regAddress.flat;
      }

      if (homePhone != null) {
        this.homePhone.number = homePhone.number;
      }
      if (workingPhone != null) {
        this.workingPhone.number = workingPhone.number;
      }
      if (mobilePhone != null) {
        this.mobilePhone1.number = mobilePhone.number;
      }


      console.log("GOT DATA");
    }, error => {
      alert("Error from retrieving " + error)
    });

    console.log("FINISHED...");
  }

  mobilePhonesEmpty(): Boolean {
    let isEmpty: boolean[] = [];
    isEmpty.push(this.clientInputForm.controls['homePhone'].invalid);
    isEmpty.push(this.clientInputForm.controls['workingPhone'].invalid);
    isEmpty.push(this.clientInputForm.controls['mobilePhone'].invalid);
    for (let e of isEmpty) {
      if (!e) {
        return false;
      }
    }
    return true;
  }

  ngAfterViewInit(): void {
    this.initElements();
  }

  initElements() {
    while (this.mobilePhonesInput.pop()) ;
    this.clientInput = new ClientInput();
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
    this.mobilePhonesInput.push(this.mobilePhone1);
    this.mobilePhonesInput.push(this.workingPhone);
    this.mobilePhonesInput.push(this.homePhone);
  }
}