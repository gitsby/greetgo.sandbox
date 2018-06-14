import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {TableModel} from "../../../models/TableModel";
import {TableService} from "../../../services/TableService";
import {BehaviorSubject, Observable, of, pipe} from "rxjs/";
import {catchError, finalize, map} from "rxjs/operators";
import {MatPaginator, MatSort} from "@angular/material";
import {HttpService} from "../../../services/HttpService";

export class UsersTableCustomDatasource implements DataSource<TableModel> {

  data: TableModel[] = [];

  private tableSubject = new BehaviorSubject<TableModel[]>([]);
  private thisTable: TableModel[];
  // private loadingSubject = new BehaviorSubject<boolean>(false);

  // public loading$ = this.loadingSubject.asObservable();


  constructor(private httpService:HttpService){
    // super();
  }

  connect(/*collectionViewer: CollectionViewer*/): Observable<TableModel[]>{
    return this.tableSubject.asObservable();
  }

  disconnect(/*collectionViewer: CollectionViewer*/): void{
    this.tableSubject.complete();
    // this.loadingSubject.complete();
  }


  loadTable(skipNumber:number,limit=4,
            sortDirection='asc', sortType='fullName'){
    // this.loadingSubject.next(true);

    this.httpService.get("/table/get-table-data",
      {
        skipNumber: skipNumber, limit: limit,
        sortDirection: sortDirection, sortType: sortType
      }).subscribe(table =>{
        this.thisTable=table.json().map(TableModel.copy);
        this.tableSubject.next(this.thisTable);
    });
      // .pipe(map(res => res["payload"]));
    // ;subscribe(response => console.log(response.text()));


    // this.tableService.retrieveTable(skipNumber, limit, sortDirection, sortType).pipe(
    //   // catchError(()=>of([])),
    //   // finalize(()=>this.loadingSubject.next(false))
    // )
    //   .subscribe(tableModel =>  this.tableSubject.next(tableModel));
  }
}
