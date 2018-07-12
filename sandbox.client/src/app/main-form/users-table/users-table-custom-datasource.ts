import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {TableModel} from "../../../models/TableModel";
import {BehaviorSubject, Observable, of, pipe} from "rxjs/";
import {catchError, finalize, map} from "rxjs/operators";
import {MatPaginator, MatSort} from "@angular/material";
import {HttpService} from "../../../services/HttpService";



export class UsersTableCustomDatasource implements DataSource<TableModel> {

  // public data: TableModel[] = [];
  public size:number=0;
  public tableSubject = new BehaviorSubject<TableModel[]>([]);
  public thisTable: TableModel[];

  constructor(private fetchedTable){
  }

  connect(): Observable<TableModel[]>{
    return this.tableSubject.asObservable();
  }

  disconnect(): void{
    this.tableSubject.complete();
  }


  loadTable()
  {
    this.fetchedTable.subscribe(table=>{
      let data=table.json();
      this.thisTable =data.table.map(TableModel.copy);
      this.size = data.size;
      this.tableSubject.next(this.thisTable);
    });

    // this.tableService.getOneValue("/table/get-table-size").then(res=>this.size=parseInt(res));
  }
}
