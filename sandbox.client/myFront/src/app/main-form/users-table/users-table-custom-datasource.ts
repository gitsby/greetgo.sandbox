import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {TableModel} from "../../../models/TableModel";
import {TableService} from "../../../services/TableService";
import {BehaviorSubject, Observable, of} from "rxjs/";
import {catchError, finalize} from "rxjs/operators";

export class UsersTableCustomDatasource implements DataSource<TableModel> {

  private tableSubject = new BehaviorSubject<TableModel[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);

  public loading$ = this.loadingSubject.asObservable();


  constructor(private tableService: TableService){}

  connect(collectionViewer: CollectionViewer): Observable<TableModel[]>{
    return this.tableSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void{
    this.tableSubject.complete();
    this.loadingSubject.complete();
  }


  loadTable(skipNumber:number,limit=4,
            sortDirection='asc', sortType='fullName'){
    this.loadingSubject.next(true);

    this.tableService.retrieveTable(skipNumber, limit, sortDirection, sortType).pipe(
      catchError(()=>of([])),
      finalize(()=>this.loadingSubject.next(false))
    )
      .subscribe(tableModel =>  this.tableSubject.next(tableModel));
  }
}
