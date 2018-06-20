import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {TableModel} from "../../../models/TableModel";
import {TableService} from "../../../services/TableService";
import {BehaviorSubject, Observable, of, pipe} from "rxjs/";
import {catchError, finalize, map} from "rxjs/operators";
import {MatPaginator, MatSort} from "@angular/material";
import {HttpService} from "../../../services/HttpService";



export class UsersTableCustomDatasource implements DataSource<TableModel> {

  // public data: TableModel[] = [];
  public size:number=0;
  public tableSubject = new BehaviorSubject<TableModel[]>([]);
  public thisTable: TableModel[];

  constructor(private tableService:TableService){
  }

  connect(): Observable<TableModel[]>{
    return this.tableSubject.asObservable();
  }

  disconnect(): void{
    this.tableSubject.complete();
  }

  getLastId(setId){
    this.tableService.getOneValue("/table/get-last-id").then(res=> setId(res));
  }

  loadTable(pageIndex=0,pageSize=3,
            sortDirection='asc', active='fullName'){
    let skipNumber = (pageIndex*pageSize);

    this.tableService.retrieveArrayOfData("/table/get-table-data",{skipNumber: skipNumber, limit: pageSize,
      sortDirection: sortDirection, sortType: active}).subscribe(
      table => {
        this.thisTable = table.json().map(TableModel.copy);
        this.tableSubject.next(this.thisTable);
      });
    this.tableService.getOneValue("/table/get-table-size").then(res=>this.size=parseInt(res));
  }
}
