import {Component, Input} from "@angular/core";
import {HttpService} from "../HttpService";
import "rxjs/add/operator/catch";
import "rxjs/add/operator/map";
import "rxjs/add/operator/toPromise";
import {Client} from "../../model/Client";
import {Address} from "../../model/Address";
import {Phone} from "../../model/Phone";

@Component({
    selector: 'edit-form-component',
    template: require('./edit_form.component.html'),
    styles: [require('./edit_form.component.css')],
})

export class EditFormComponent {

    @Input() client: Client;

    constructor(private httpService: HttpService) {
    }

    saveClient() {
        // Required
        let nameInput = document.getElementById("nameInput") as HTMLInputElement;
        let surnameInput = document.getElementById("surnameInput") as HTMLInputElement;
        let patronymicInput = document.getElementById("patronymicInput") as HTMLInputElement;
        let genderInput = document.getElementById("genderInput") as HTMLInputElement;
        let birthDateInput = document.getElementById("dateInput") as HTMLInputElement;

        let houseInput = document.getElementById("houseInput") as HTMLInputElement;
        let streetInput = document.getElementById("streetInput") as HTMLInputElement;
        let flatInput = document.getElementById("flatInput") as HTMLInputElement;

        // Non Required
        let factStreetInput = document.getElementById("factStreetInput") as HTMLInputElement;
        let factHouseInput = document.getElementById("factHouseInput") as HTMLInputElement;
        let factFlatInput = document.getElementById("factFlatInput") as HTMLInputElement;

        let homePhoneInput = document.getElementById("homePhoneInput") as HTMLInputElement;
        let workingPhoneInput = document.getElementById("workingPhoneInput") as HTMLInputElement;
        let mobilePhonesInput = [];
        mobilePhonesInput.push(document.getElementById("mobilePhone1Input") as HTMLInputElement);
        mobilePhonesInput.push(document.getElementById("mobilePhone2Input") as HTMLInputElement);
        mobilePhonesInput.push(document.getElementById("mobilePhone3Input") as HTMLInputElement);

        if (nameInput.value.length == 0 || surnameInput.value.length == 0 || patronymicInput.value.length == 0 ||
            genderInput.value.length == 0 || houseInput.value.length == 0 || streetInput.value.length == 0 ||
            flatInput.value.length == 0 || birthDateInput.value.length == 0 ||
            this.mobilePhonesEmpty(mobilePhonesInput)) {
            alert("You must fill all required fields!");
            return;
        }
        let newClient: Client = new Client();
        newClient.id = -1;
        newClient.name = nameInput.value;
        newClient.surname = surnameInput.value;
        newClient.patronymic = patronymicInput.value;
        newClient.gender = genderInput.value;
        newClient.birthDate = birthDateInput.value;

        let addresses: Address[] = [];

        let address: Address = new Address();
        address.type = "REG";
        address.street = streetInput.value;
        address.house = houseInput.value;
        address.flat = flatInput.value;
        addresses.push(address);

        if (factStreetInput.value.length != 0 && factHouseInput.value.length != 0
            && factFlatInput.value.length != 0) {
            let factAddress = new Address();
            factAddress.house = factHouseInput.value;
            factAddress.street = factStreetInput.value;
            factAddress.flat = factFlatInput.value;
            factAddress.type = "FACT";
            addresses.push(factAddress);
        }


        let phones: Phone[] = [];

        if (homePhoneInput.value.length != 0) {
            let homePhone = new Phone();
            homePhone.number = homePhoneInput.value;
            homePhone.type = "HOME";
            phones.push(homePhone);
        }

        if (workingPhoneInput.value.length != 0) {
            let workingPhone = new Phone();
            workingPhone.number = workingPhoneInput.value;
            workingPhone.type = "WORKING";
            phones.push(workingPhone);
        }

        for (let mobilePhone of mobilePhonesInput) {
            if (mobilePhone.value.length != 0) {
                let newPhone = new Phone();
                newPhone.number = mobilePhone.value;
                newPhone.type = "MOBILE";
                phones.push(newPhone);
            }
        }
        newClient.addresses = addresses;
        newClient.phones = phones;
        //
        this.httpService.get("/auth/add_client", {newClient: newClient.toString()}).toPromise().then(result => {
            alert("ADDED");
        }, error => {
            alert(error)
        });
    }

    mobilePhonesEmpty(mobilePhones): Boolean {
        for (let mobilePhone of mobilePhones) {
            if (mobilePhone.value.length != 0) {
                return false;
            }
        }
        return true;
    }

    closeModalForm() {
        document.getElementById("myModal").style.display = "none";
    }

}