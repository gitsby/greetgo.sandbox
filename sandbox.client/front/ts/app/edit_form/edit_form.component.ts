import {AfterViewInit, Component, EventEmitter, Output} from "@angular/core";
import {HttpService} from "../HttpService";
import "rxjs/add/operator/catch";
import "rxjs/add/operator/map";
import "rxjs/add/operator/toPromise";
import {ClientToSave} from "../../model/ClientToSave";
import {Address} from "../../model/Address";
import {Phone} from "../../model/Phone";
import {Character} from "../../model/Character";
import {EditClient} from "../../model/EditClient";
import {ClientInput} from "../../model/ClientInput";

let retrievedClient: ClientToSave;

@Component({
    selector: 'edit-form-component',
    template: require('./edit_form.component.html'),
    styles: [require('./edit_form.component.css')],
})

export class EditFormComponent implements AfterViewInit {


    clientInput: ClientInput = new ClientInput();
    factAddress: Address = new Address();
    regAddress: Address = new Address();
    homePhone: Phone = new Phone();
    workingPhone: Phone = new Phone();
    mobilePhone1: Phone = new Phone();
    mobilePhone2: Phone = new Phone();
    mobilePhone3: Phone = new Phone();

    public clientId: string = null;

    mobilePhonesInput = [];

    @Output() onClose = new EventEmitter<void>();
    @Output() returnChanges = new EventEmitter<any>();

    closeModalForm() {
        // this.nameInput.nativeElement.value = "";
        // this.surnameInput.nativeElement.value = "";
        //
        // this.patronymicInput.nativeElement.value = "";
        // this.genderInput.nativeElement.value = "";
        //
        // this.houseInput.nativeElement.value = "";
        // this.streetInput.nativeElement.value = "";
        // this.flatInput.nativeElement.value = "";
        //
        // this.factHouseInput.nativeElement.value = "";
        // this.factFlatInput.nativeElement.value = "";
        // this.factFlatInput.nativeElement.value = "";
        //
        //
        // this.homePhoneInput.nativeElement.value = "";
        // this.workingPhoneInput.nativeElement.value = "";
        //
        this.onClose.emit();
        // document.getElementById("myModal").style.display = "none";
    }

    constructor(private httpService: HttpService) {

    }

    saveClient() {
        if (this.clientInput.name == null || this.clientInput.surname == null || this.clientInput.patronymic == null ||
            this.clientInput.gender == null || this.regAddress.house == null || this.regAddress.street == null ||
            this.regAddress.flat == null ||
            this.mobilePhonesEmpty(this.mobilePhonesInput)) {
            alert("You must fill all required fields!");
            return;
        }
        if (this.clientId == null) { // When adding
            alert("ADD")
            let newClient: EditClient = new EditClient();
            newClient.id = null;
            newClient.name = this.clientInput.name;
            newClient.surname = this.clientInput.surname;
            newClient.patronymic = this.clientInput.patronymic;
            newClient.charm = new Character();
            newClient.gender = this.clientInput.gender;
            newClient.birthDate = this.clientInput.birthDate;

            let addresses: Address[] = [];

            addresses.push(this.regAddress);
            if (this.factAddress.street.length != 0 && this.factAddress.house.length != 0
                && this.factAddress.flat.length != 0) {
                this.factAddress.type = "FACT";
                addresses.push(this.factAddress);
            }

            let phones: Phone[] = [];

            if (this.homePhone.number != null) {
                this.homePhone.type = "HOME";
                phones.push(this.homePhone);
            }

            if (this.workingPhone.number != null) {
                this.workingPhone.type = "WORKING";
                phones.push(this.workingPhone);
            }

            for (let mobilePhone of this.mobilePhonesInput) {
                if (mobilePhone.number != null) {
                    mobilePhone.type = "MOBILE";
                    phones.push(mobilePhone);
                }
            }
            newClient.addedAddresses = addresses;
            newClient.addedPhones = phones;
            console.log(newClient.toString())
            this.httpService.get("/client/edit", {editedClient: newClient.toString()}).toPromise().then(result => {

            }, error => {
                alert(error);
            });
        } else {// When editing
            // TODO: Change edit
            alert("Edit");
            let factNum = -1;
            let regNum = 0;
            let homeNum = -1;
            let workingNum = -1;
            let mobilePhoneNumIndexes: number[] = [];
            retrievedClient.phones.forEach((val, index) => {
                alert(index)
                if (val.type == "MOBILE") {
                    mobilePhoneNumIndexes.push(index);
                }
                if (val.type == "HOME") {
                    homeNum = index;
                }
                if (val.type == "WORKING") {
                    workingNum = index;
                }
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

            let editClient: EditClient = new EditClient();
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
            if (this.clientInput.birthDate != retrievedClient.birthDate) {
                editClient.birthDate = this.clientInput.birthDate;
            }
            if (this.clientInput.gender != retrievedClient.gender) {
                editClient.gender = this.clientInput.gender;
            }

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

                    if (this.homePhone.number != retrievedClient.phones[homeNum].number) {
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
                        != retrievedClient.phones[workingNum].number) {
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
            mobilePhoneNumIndexes.forEach(value => {
                if (this.mobilePhonesInput[value].number.length != 0) {
                    if (this.mobilePhonesInput[value].number
                        != retrievedClient.phones[value].number) {
                        editClient.editedPhones.push(this.mobilePhonesInput[value]);
                    }
                } else {
                    editClient.deletedPhones.push(retrievedClient.phones[value]);
                }
            })
            alert("EDITING");
            this.httpService.get("/client/edit", {editedClient: editClient.toString()}).toPromise().then(result => {

            }, error => {
                alert(error);
            });
        }
        this.outputResults();
        // this.clientInput = new ClientInput();
        // this.factAddress = new Address();
        // this.regAddress = new Address();
        // this.homePhone = new Phone();
        // this.workingPhone = new Phone();
        // this.mobilePhone1 = new Phone();
        // this.mobilePhone2 = new Phone();
        // this.mobilePhone3 = new Phone();
    }

    outputResults(){
        this.returnChanges.emit();
    }
    public loadFromDatabase(clientId) {
        this.clientId = clientId;
        console.log("started .........")
        this.httpService.get("/client/getClientWithId",
            {clientId: this.clientId + ""}).toPromise().then(result => {
            retrievedClient = result.json();

            this.clientInput.name = retrievedClient.name;
            this.clientInput.surname = retrievedClient.surname;
            this.clientInput.patronymic = retrievedClient.patronymic;

            this.clientInput.gender = retrievedClient.gender;
            this.clientInput.birthDate = retrievedClient.birthDate;

            let homePhone: Phone = null;
            let workingPhone: Phone = null;
            let mobilePhones: Phone[] = [];

            for (let phone of retrievedClient.phones) {
                if (phone.type == "HOME") {
                    homePhone = phone;
                } else if (phone.type == "WORKING") {
                    workingPhone = phone;
                } else if (phone.type == "MOBILE") {
                    mobilePhones.push(phone);
                }
            }

            let factAddress: Address = null;
            let regAddress: Address = null;
            for (let address of retrievedClient.addresses) {
                if (address.type == "FACT") {
                    factAddress = address;
                } else if (address.type == "REG") {
                    regAddress = address;
                }
            }

            if (factAddress != null) {
                this.factAddress.flat = factAddress.flat;
                this.factAddress.house = factAddress.house;
                this.factAddress.street = factAddress.street;
            }

            if (regAddress != null) {
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

            mobilePhones.forEach((item, index) => {
                this.mobilePhonesInput[index].number = item.number;
            });


            console.log("GOT DATA");
        }, error => {
            alert("Error from retrieving " + error)
        });

        console.log("FINISHED...");
    }

    mobilePhonesEmpty(mobilePhones): Boolean {
        for (let mobilePhone of mobilePhones) {
            if (mobilePhone.number != null) {
                return false;
            }
        }
        return true;
    }

    ngAfterViewInit(): void {
        this.initElements();
    }

    initElements() {
        this.regAddress.type = "REG";
        this.factAddress.type = "FACT";
        this.homePhone.type = "HOME";
        this.workingPhone.type = "WORKING";
        this.mobilePhonesInput.push(this.mobilePhone1);
        this.mobilePhonesInput.push(this.mobilePhone2);
        this.mobilePhonesInput.push(this.mobilePhone3);
    }
}