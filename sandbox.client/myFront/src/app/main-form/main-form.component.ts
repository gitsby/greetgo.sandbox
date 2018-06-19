import {Component, EventEmitter, Output, ViewChild} from "@angular/core";
import { UserInfo } from "../../models/UserInfo";
import { HttpService } from "../../services/HttpService";
import { PhoneType } from "../../models/PhoneType";
import { User } from "../../models/User";
import { Phone } from "../../models/Phone";
import { Address } from "../../models/Address";
import { CharmType } from "../../models/CharmType";
import { MatDialogRef,MatDialog, MatDialogConfig } from '@angular/material';
import { UserDialogComponent } from './user-dialog/user-dialog.component';
import {UsersTableComponent} from "./users-table/users-table.component";

@Component({
  selector: 'main-form-component',
  templateUrl: './main-form.component.html',
})
export class MainFormComponent {
  @Output() exit = new EventEmitter<void>();

  userInfo: UserInfo | null = null;
  loadUserInfoButtonEnabled: boolean = true;
  loadUserInfoError: string | null;
  mockRequest: string | null = null;
  userIsLoading: boolean | null = false;
  selectedUserID: string = '0';
  selectedUser: User = this.generateNewUser();
  isThereData: boolean = true;
  typeOfDialogCall: string | null = null;
  userDialogRef: MatDialogRef<UserDialogComponent>;
  @ViewChild(UsersTableComponent) private usersTableComponent: UsersTableComponent;

  constructor(private httpService: HttpService, private dialog: MatDialog) {}

  selectedUserIDChange(changedUser) {
    this.isThereData = true;
    this.selectedUserID = changedUser;
  }

  openDialog(titleType: string) {
    this.userIsLoading=true;
    let clearlyOpenDialog=(user) => this.dialog.open(UserDialogComponent, {
      hasBackdrop: true,
      minWidth: 400,
      data: {
        user: user,
        titleType: titleType
      }
    });

    if(titleType==='Update')
      this.getSelectedUser(clearlyOpenDialog);
    else{
      clearlyOpenDialog(this.generateNewUser());
    }
    this.dialog.afterAllClosed.subscribe(() => this.usersTableComponent.loadTablePage());
  }


  deleteButtonClicked(): void {
    this.httpService.post("/table/delete-user", {"userID": this.selectedUserID}).toPromise().then(res => {
      alert(res.text());
      this.usersTableComponent.loadTablePage();
    });

  }

  updateButtonClicked(): void {
    this.openDialog("Update");
  }

  createButtonClicked(): void {
    this.openDialog("Create");
  }

  getSelectedUser(callback) {
    return (this.httpService.get('/table/get-exact-user', {'userID': this.selectedUserID}).toPromise().then(
      res => {
        this.selectedUser = User.copy(res.json());
        this.isThereData = false;
        callback(this.selectedUser);
        this.userIsLoading=false;
      }
    ));
  }


  generateNewUser(): User {
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
    return user
  }
}
