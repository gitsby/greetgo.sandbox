import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {TableModel} from "../../../models/TableModel";
import {TableService} from "../../../services/TableService";
import {BehaviorSubject, Observable, of, pipe} from "rxjs/";
import {catchError, finalize, map} from "rxjs/operators";
import {MatPaginator, MatSort} from "@angular/material";
import {HttpService} from "../../../services/HttpService";

export class UsersTableCustomDatasource implements DataSource<TableModel> {

  data: TableModel[] = [];
  public size:number=0;
  private tableSubject = new BehaviorSubject<TableModel[]>([]);
  private thisTable: TableModel[];

  constructor(private httpService:HttpService){
  }

  connect(): Observable<TableModel[]>{
    return this.tableSubject.asObservable();
  }

  disconnect(): void{
    this.tableSubject.complete();
  }

  loadTable(pageIndex=0,pageSize=3,
            sortDirection='asc', active='fullName'){
    let skipNumber = (pageIndex*pageSize);
    this.httpService.get("/table/get-table-data",
      {skipNumber: skipNumber, limit: pageSize,
        sortDirection: sortDirection, sortType: active})
        .subscribe(table =>{
        this.thisTable=table.json().map(TableModel.copy);
        this.tableSubject.next(this.thisTable);
    });
    this.httpService.get('/table/get-table-size').toPromise().then(
      response => this.size= parseInt(response.text())
    );
  }
}
