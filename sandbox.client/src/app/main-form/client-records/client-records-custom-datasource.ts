import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {ClientRecord} from "../../../models/ClientRecord";
import {BehaviorSubject, Observable, of, pipe} from "rxjs/";
import {catchError, finalize, map} from "rxjs/operators";
import {MatPaginator, MatSort} from "@angular/material";
import {HttpService} from "../../../services/HttpService";



export class ClientRecordsCustomDatasource implements DataSource<ClientRecord> {

  public size:number=0;
  public clientRecordsSubject = new BehaviorSubject<ClientRecord[]>([]);
  public thisClientRecords: ClientRecord[];

  constructor(private fetchedClientRecords){
  }

  connect(): Observable<ClientRecord[]>{
    return this.clientRecordsSubject.asObservable();
  }

  disconnect(): void{
    this.clientRecordsSubject.complete();
  }


  loadClientRecords()
  {
    this.fetchedClientRecords.subscribe(response=>{

      let data=response.json();
      console.log(response.json());
      this.thisClientRecords =data.table.map(ClientRecord.copy);
      this.size = data.size;
      this.clientRecordsSubject.next(this.thisClientRecords);
    });

  }
}
