import {AfterViewInit, Component, ElementRef, EventEmitter, Output, ViewChild} from "@angular/core";
import {HttpService} from "../HttpService";
import "rxjs/add/operator/catch";
import "rxjs/add/operator/map";
import "rxjs/add/operator/toPromise";
import {Client} from "../../model/Client";
import {Address} from "../../model/Address";
import {Phone} from "../../model/Phone";
import {Character} from "../../model/Character";
import {EditClient} from "../../model/EditClient";

let retrievedClient: Client;

@Component({
    selector: 'edit-form-component',
    template: require('./edit_form.component.html'),
    styles: [require('./edit_form.component.css')],
})

export class EditFormComponent implements AfterViewInit {
    @ViewChild("anyP") anyP;
    @ViewChild("nameInput") nameInput: ElementRef;
    @ViewChild("surnameInput") surnameInput: ElementRef;
    @ViewChild("patronymicInput") patronymicInput: ElementRef;
    @ViewChild("genderInput") genderInput: ElementRef;
    @ViewChild("dateInput") birthDateInput: ElementRef;

    @ViewChild("houseInput") houseInput: ElementRef;
    @ViewChild("streetInput") streetInput: ElementRef;
    @ViewChild("flatInput") flatInput: ElementRef;

    @ViewChild("factStreetInput") factStreetInput: ElementRef;
    @ViewChild("factHouseInput") factHouseInput: ElementRef;
    @ViewChild("factFlatInput") factFlatInput: ElementRef;

    @ViewChild("homePhoneInput") homePhoneInput: ElementRef;
    @ViewChild("workingPhoneInput") workingPhoneInput: ElementRef;
    @ViewChild("mobilePhone1Input") mobilePhone1Input: ElementRef;

    @ViewChild("mobilePhone2Input") mobilePhone2Input: ElementRef;
    @ViewChild("mobilePhone3Input") mobilePhone3Input: ElementRef;

    public client = null;

    mobilePhonesInput = [];

    @Output() onClose = new EventEmitter<void>();

    closeModalForm() {
        this.nameInput.nativeElement.value = "";
        this.surnameInput.nativeElement.value = "";

        this.patronymicInput.nativeElement.value = "";
        this.genderInput.nativeElement.value = "";

        this.houseInput.nativeElement.value = "";
        this.streetInput.nativeElement.value = "";
        this.flatInput.nativeElement.value = "";

        this.factHouseInput.nativeElement.value = "";
        this.factFlatInput.nativeElement.value = "";
        this.factFlatInput.nativeElement.value = "";


        this.homePhoneInput.nativeElement.value = "";
        this.workingPhoneInput.nativeElement.value = "";

        this.onClose.emit();
        // document.getElementById("myModal").style.display = "none";
    }

    constructor(private httpService: HttpService) {

    }

    saveClient() {
        if (this.nameInput.nativeElement.value.length == 0 || this.surnameInput.nativeElement.value.length == 0 || this.patronymicInput.nativeElement.value.length == 0 ||
            this.genderInput.nativeElement.value.length == 0 || this.houseInput.nativeElement.value.length == 0 || this.streetInput.nativeElement.value.length == 0 ||
            this.flatInput.nativeElement.value.length == 0 ||
            this.mobilePhonesEmpty(this.mobilePhonesInput)) {
            alert("You must fill all required fields!");
            return;
        }
        if (this.client == null) {
            let newClient: Client = new Client();
            newClient.id = -1;
            newClient.name = this.nameInput.nativeElement.value;
            newClient.surname = this.surnameInput.nativeElement.value;
            newClient.patronymic = this.patronymicInput.nativeElement.value;
            newClient.charm = new Character();
            newClient.gender = this.genderInput.nativeElement.value;
            newClient.birthDate = this.birthDateInput.nativeElement.value;

            let addresses: Address[] = [];

            addresses.push(this.getRegAddress());

            if (this.factStreetInput.nativeElement.value.length != 0 && this.factHouseInput.nativeElement.value.length != 0
                && this.factFlatInput.nativeElement.value.length != 0) {
                addresses.push(this.getFactAddress());
            }

            let phones: Phone[] = [];

            if (this.homePhoneInput.nativeElement.value.length != 0) {
                let homePhone = new Phone();
                homePhone.number = this.homePhoneInput.nativeElement.value;
                homePhone.type = "HOME";
                phones.push(homePhone);
            }

            if (this.workingPhoneInput.nativeElement.value.length != 0) {
                let workingPhone = new Phone();
                workingPhone.number = this.workingPhoneInput.nativeElement.value;
                workingPhone.type = "WORKING";
                phones.push(workingPhone);
            }

            for (let mobilePhone of this.mobilePhonesInput) {
                if (mobilePhone.nativeElement.value.length != 0) {
                    let newPhone = new Phone();
                    newPhone.number = mobilePhone.nativeElement.value;
                    newPhone.type = "MOBILE";
                    phones.push(newPhone);
                }
            }
            newClient.addresses = addresses;
            newClient.phones = phones;
            //
            this.httpService.get("/client/add_client", {newClient: newClient.toString()}).toPromise().then(result => {
                if (result) {
                    alert("Added");
                } else {
                    alert("Unable to add!");
                }
            }, error => {
                alert(error)
            });
        } else {
            // TODO: Change edit
            alert("Edit");
            let factNum = -1;
            let regNum = 0;
            let homeNum = -1;
            let workingNum = -1;
            let mobilePhoneNumIndexes: number[] = [];
            retrievedClient.phones.forEach((val, index) => {
                if (val.type = "MOBILE") {
                    mobilePhoneNumIndexes.push(index);
                }
                if (val.type = "HOME") {
                    homeNum = index;
                }
                if (val.type = "WORKING") {
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

            let editClient: EditClient = new EditClient();
            if (retrievedClient.name != this.nameInput.nativeElement.value) {
                editClient.name = this.nameInput.nativeElement.value;
            }
            if (retrievedClient.surname != this.surnameInput.nativeElement.value) {
                editClient.name = this.surnameInput.nativeElement.value;
            }
            if (retrievedClient.patronymic != this.patronymicInput.nativeElement.value) {
                editClient.patronymic = this.patronymicInput.nativeElement.value;
            }
            if (this.birthDateInput.nativeElement.value != retrievedClient.patronymic) {
                editClient.birthDate = this.birthDateInput.nativeElement.value;
            }
            if (this.factHouseInput.nativeElement.value.size != 0
                && this.factStreetInput.nativeElement.value.size != 0
                && this.factFlatInput.nativeElement.value.size != 0) {
                if (factNum != -1) {
                    if (retrievedClient.addresses[factNum].street != this.factStreetInput.nativeElement.value
                        || retrievedClient.addresses[factNum].house != this.factHouseInput.nativeElement.value
                        || retrievedClient.addresses[factNum].flat != this.factFlatInput.nativeElement.value) {
                        let newFact = this.getFactAddress();
                        newFact.clientId = retrievedClient.id;
                        editClient.editedAddresses.push(newFact);
                    }
                } else {
                    let newFact = this.getFactAddress();
                    newFact.clientId = retrievedClient.id;
                    editClient.addedAddresses.push(newFact);
                }
            } else {
                if (factNum != -1) {
                    editClient.deletedAddresses.push(retrievedClient.addresses[factNum]);
                }
            }
            if (this.houseInput.nativeElement.value.size != 0
                && this.streetInput.nativeElement.value.size != 0
                && this.flatInput.nativeElement.value.size != 0) {
                if (retrievedClient.addresses[regNum].street != this.streetInput.nativeElement.value
                    || retrievedClient.addresses[regNum].house != this.houseInput.nativeElement.value
                    || retrievedClient.addresses[regNum].flat != this.flatInput.nativeElement.value) {
                    let newFact = this.getRegAddress();
                    newFact.clientId = retrievedClient.id;
                    editClient.editedAddresses.push(newFact);
                }
            } else {
                alert("Register Address must not be empty");
            }
            alert("EDITING");
            this.httpService.get("/client/edit", {editedClient: editClient.toString()}).toPromise().then(result => {

            }, error => {
                alert(error);
            });
        }

    }

    getFactAddress(): Address {
        let newFact: Address = new Address();
        newFact.street = this.factStreetInput.nativeElement.value;
        newFact.house = this.factHouseInput.nativeElement.value;
        newFact.flat = this.factFlatInput.nativeElement.value;
        newFact.type = "FACT";
        return newFact;
    }

    getRegAddress(): Address {
        let newReg: Address = new Address();
        newReg.street = this.streetInput.nativeElement.value;
        newReg.house = this.houseInput.nativeElement.value;
        newReg.flat = this.flatInput.nativeElement.value;
        newReg.type = "REG";
        return newReg;
    }

    public loadFromDatabase(clientId) {
        this.client = clientId;
        console.log("started .........")
        this.httpService.get("/client/getClientWithId",
            {clientId: this.client + ""}).toPromise().then(result => {
            retrievedClient = result.json();

            this.nameInput.nativeElement.value = retrievedClient.name;
            this.surnameInput.nativeElement.value = retrievedClient.surname;
            this.patronymicInput.nativeElement.value = retrievedClient.patronymic;

            this.genderInput.nativeElement.value = "aSDASD";
            this.birthDateInput.nativeElement.value = retrievedClient.birthDate;

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
                this.factFlatInput.nativeElement.value = factAddress.flat;
                this.factHouseInput.nativeElement.value = factAddress.house;
                this.factStreetInput.nativeElement.value = factAddress.street;
            }

            if (regAddress != null) {
                this.houseInput.nativeElement.value = regAddress.house;
                this.streetInput.nativeElement.value = regAddress.street;
                this.flatInput.nativeElement.value = regAddress.flat;
            }

            if (homePhone != null) {
                this.homePhoneInput.nativeElement.value = homePhone.number;
            }
            if (workingPhone != null) {
                this.workingPhoneInput.nativeElement.value = workingPhone.number;
            }

            mobilePhones.forEach((item, index) => {
                this.mobilePhonesInput[index].nativeElement.value = item.number;
            });


            console.log("GOT DATA");
        }, error => {
            alert("Error from retrieving " + error)
        });

        console.log("FINISHED...");
    }

    mobilePhonesEmpty(mobilePhones): Boolean {
        for (let mobilePhone of mobilePhones) {
            if (mobilePhone.nativeElement.value.length != 0) {
                return false;
            }
        }
        return true;
    }

    ngAfterViewInit(): void {
        this.mobilePhonesInput.push(this.mobilePhone1Input);
        this.mobilePhonesInput.push(this.mobilePhone2Input);
        this.mobilePhonesInput.push(this.mobilePhone3Input);
    }


}