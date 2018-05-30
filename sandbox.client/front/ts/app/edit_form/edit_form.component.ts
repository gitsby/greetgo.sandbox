import {Component, Input} from "@angular/core";
import {HttpService} from "../HttpService";
import "rxjs/add/operator/catch";
import "rxjs/add/operator/map";
import "rxjs/add/operator/toPromise";
import {Client} from "../../model/Client";
import {Address} from "../../model/Address";

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

        let houseInput = document.getElementById("houseInput") as HTMLInputElement;
        let streetInput = document.getElementById("streetInput") as HTMLInputElement;
        let flatInput = document.getElementById("flatInput") as HTMLInputElement;

        // Non Required
        let factStreetInput = document.getElementById("factStreetInput") as HTMLInputElement;
        let factHouseInput = document.getElementById("factHouseInput") as HTMLInputElement;
        let factFlatInput = document.getElementById("factFlatInput") as HTMLInputElement;

        let homePhoneInput = document.getElementById("homePhoneInput") as HTMLInputElement;
        let workingPhoneInput = document.getElementById("workingPhoneInput") as HTMLInputElement;
        let mobilePhone1Input = document.getElementById("mobilePhone1Input") as HTMLInputElement;
        let mobilePhone2Input = document.getElementById("mobilePhone2Input") as HTMLInputElement;
        let mobilePhone3Input = document.getElementById("mobilePhone3Input") as HTMLInputElement;


        if (nameInput.value.length == 0 || surnameInput.value.length == 0 || patronymicInput.value.length == 0 ||
            genderInput.value.length == 0 || houseInput.value.length == 0 || streetInput.value.length == 0 ||
            flatInput.value.length == 0 ||
            (mobilePhone1Input.value.length == 0 && mobilePhone2Input.value.length == 0 &&
                mobilePhone3Input.value.length == 0)) {
            alert("You must fill all required fields!");
            return;
        }
        let newClient:Client = new Client();
        newClient.name = nameInput.value;
        newClient.surname = surnameInput.value;
        newClient.patronymic = patronymicInput.value;
        newClient.gender = genderInput.value;

        let adresses: Address[] = [];

        let address: Address = new Address();
        address.clientId = newClient.id;
        address.type = "Reg";
        address.street = streetInput.value;
        address.house = houseInput.value;
        address.flat = flatInput.value;

        adresses.push(address);

    }

    closeModalForm() {
        document.getElementById("myModal").style.display = "none";
    }

}