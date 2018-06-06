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


    characters: Character[] = []
    characterInput = '';
    clientInput: ClientInput = new ClientInput();
    factAddress: Address = new Address();
    regAddress: Address = new Address();
    homePhone: Phone = new Phone();
    workingPhone: Phone = new Phone();
    mobilePhone1: Phone = new Phone();

    public clientId: string = null;

    mobilePhonesInput = [];

    @Output() onClose = new EventEmitter<void>();
    @Output() returnChanges = new EventEmitter<any>();

    closeModalForm() {
        this.initElements();
        this.onClose.emit();
        // document.getElementById("myModal").style.display = "none";
    }

    constructor(private httpService: HttpService) {
        httpService.get("/client/characters").toPromise().then(result => {
            for (let chars of result.json()) {
                console.log(chars.name);
                this.characters.push(chars as Character)
            }
        }, error => {
            alert(error)
        })
    }

    saveClient() {

        if (this.clientInput.name == null || this.clientInput.surname == null || this.clientInput.patronymic == null ||
            this.clientInput.gender == null || this.regAddress.house == null || this.regAddress.street == null ||
            this.regAddress.flat == null || this.clientInput.birthDate == null || this.clientInput.character == -1 ||
            this.mobilePhonesEmpty(this.mobilePhonesInput)) {
            alert("You must fill all required fields!");
            return;
        }

        let editClient: EditClient = new EditClient();
        if (this.clientId == null) { // When adding
            alert("ADD");
            editClient.id = null;
            editClient.name = this.clientInput.name;
            editClient.surname = this.clientInput.surname;
            editClient.patronymic = this.clientInput.patronymic;

            editClient.charm = this.characters[this.clientInput.character].id;
            editClient.gender = this.clientInput.gender;
            editClient.birthDate = this.clientInput.birthDate;

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

            if (this.homePhone.number != null) {
                this.homePhone.type = "HOME";
                phones.push(this.homePhone);
            }

            if (this.workingPhone.number != null) {
                this.workingPhone.type = "WORKING";
                phones.push(this.workingPhone);
            }
            if (this.mobilePhone1.number != null) {
                this.mobilePhone1.type = "MOBILE";
                phones.push(this.workingPhone);
            }

            editClient.addedAddresses = addresses;
            editClient.addedPhones = phones;
            console.log(editClient.toString())
            this.httpService.get("/client/edit", {editedClient: editClient.toString()}).toPromise().then(result => {

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
            let mobilePhoneNum = -1;
            retrievedClient.phones.forEach((val, index) => {
                alert(index)
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
            retrievedClient.addresses.forEach((val, index) => {
                if (val.type == "FACT") {
                    factNum = index;
                } else {
                    regNum = index;
                }
            });
            console.log("First " + retrievedClient.phones[mobilePhoneNum].number + " " + this.mobilePhone1.editedTo);
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
            if (this.clientInput.birthDate != retrievedClient.birthDate) {
                editClient.birthDate = this.clientInput.birthDate;
            }
            if (this.clientInput.gender != retrievedClient.gender) {
                editClient.gender = this.clientInput.gender;
            }
            if (this.clientInput.gender != retrievedClient.gender) {
                editClient.gender = this.clientInput.gender;
            }
            console.log("Second " + retrievedClient.phones[mobilePhoneNum].number + " " + this.mobilePhone1.editedTo);

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
            console.log("Third " + retrievedClient.phones[mobilePhoneNum].number + " " + this.mobilePhone1.editedTo);

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
            console.log("Fourth " + retrievedClient.phones[mobilePhoneNum].number + " " + this.mobilePhone1.editedTo);

            if (homeNum != -1) {
                if (this.homePhone.number.length != 0) {
                    if (this.homePhone.number != retrievedClient.phones[homeNum].number) {
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
            console.log("Fifth " + retrievedClient.phones[mobilePhoneNum].number + " " + this.mobilePhone1.editedTo);

            if (workingNum != -1) {
                if (this.workingPhone.number.length != 0) {
                    if (this.workingPhone.number
                        != retrievedClient.phones[workingNum].number) {
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
            console.log("Sixth " + retrievedClient.phones[mobilePhoneNum].number + " " + this.mobilePhone1.editedTo);

            if (mobilePhoneNum != -1) {
                if (this.mobilePhone1.number.length != 0) {
                    console.log("Seventh " + retrievedClient.phones[mobilePhoneNum].number + " " + this.mobilePhone1.editedTo);

                    if (this.mobilePhone1.number
                        != retrievedClient.phones[mobilePhoneNum].number) {
                        let number = this.mobilePhone1.number;
                        this.mobilePhone1.number = retrievedClient.phones[mobilePhoneNum].number;
                        this.mobilePhone1.editedTo = number;
                        console.log("Eight " + retrievedClient.phones[mobilePhoneNum].number + " " + this.mobilePhone1.editedTo);

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

            console.log(editClient.toString())

            alert("EDITING");
            this.httpService.get("/client/edit", {editedClient: editClient.toString()}).toPromise().then(result => {

            }, error => {
                alert(error);
            });
        }
        this.outputResults(editClient);
    }

    outputResults(editClient: EditClient) {
        this.returnChanges.emit(editClient);
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
            alert(retrievedClient.charm);
            this.clientInput.character = this.characters[retrievedClient.charm].id;
            this.clientInput.gender = retrievedClient.gender;
            this.clientInput.birthDate = retrievedClient.birthDate;

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
                console.log("RETRIVED MOBILE: " + mobilePhone.number);
                this.mobilePhone1.number = mobilePhone.number;
            }


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