import { Component, EventEmitter, OnDestroy, Output, ViewChild} from "@angular/core";
import { UserInfo } from "../../models/UserInfo";
import { HttpService } from "../../services/HttpService";
import { MatDialogRef,MatDialog, MatDialogConfig } from '@angular/material';
import { ClientDialogComponent } from './client-dialog/client-dialog.component';
import { ClientRecordsComponent } from "./client-records/client-records.component";
import { Subscription } from "rxjs/";
import {CharmService} from "../../services/CharmService";

@Component({
  selector: 'main-form-component',
  // providers: [charmService]
  templateUrl: './main-form.component.html',
})
export class MainFormComponent implements OnDestroy{
  @Output() exit = new EventEmitter<void>();

  subscription: Subscription;

  userInfo: UserInfo | null = null;
  loadClientInfoButtonEnabled: boolean = true;
  loadClientInfoError: string | null;
  mockRequest: string | null = null;
  // clientIsLoading: boolean = false;
  selectedClientId: number = -1;
  // selectedClient: Client = this.generateNewClient();
  // isThereData: boolean = true;
  typeOfDialogCall: string | null = null;
  clientDialogRef: MatDialogRef<ClientDialogComponent>;
  // http: Http;
  @ViewChild(ClientRecordsComponent) private clientRecordsComponent: ClientRecordsComponent;

  constructor(private httpService: HttpService, private dialog: MatDialog,
              private charmService: CharmService) {}

  ngOnInit(){
    this.charmService.getCharms();
  }

  selectedClientIdChange(changedClient) {
    this.selectedClientId = changedClient;
    //console.log(this.selectedClientId);
  }

  openDialog(id = null) {
      this.clientDialogRef = this.dialog.open(ClientDialogComponent, {
        hasBackdrop: true,
        minWidth: 400,
        disableClose: true,
        data: {
          id:id
        }
      });

      this.clientDialogRef.afterClosed().subscribe((data:object)=> {
        //console.log(typeof data);
        //console.log(data);
        //console.log(data["client"]);
        if (data["state"]===true) {
          let client = data["client"];
          if (id === null) {

            this.selectedClientId = parseInt(client.id);
            this.clientRecordsComponent.addOneRow(client,data["charm"]);
          } else {
            this.clientRecordsComponent.updateOneRow(client,data["charm"]);
          }
        }

      });

    }


  deleteButtonClicked(): void {
    this.httpService.post("/client-records/delete-client", {"clientId": this.selectedClientId}).toPromise().then(res => {
      //console.log(res.json());
      this.clientRecordsComponent.loadClientRecordsPage();
    });
  }

  updateButtonClicked(): void {
    this.openDialog(this.selectedClientId);
  }

  createButtonClicked(): void {
    this.openDialog(null);
  }


  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
