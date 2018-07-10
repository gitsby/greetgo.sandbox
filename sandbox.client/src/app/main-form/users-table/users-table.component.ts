import {ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {MatPaginator, MatSort} from '@angular/material';
import { UsersTableCustomDatasource} from "./users-table-custom-datasource";
import { HttpService} from "../../../services/HttpService";
import {tap} from "rxjs/operators";
import {TableModel} from "../../../models/TableModel";
import {User} from "../../../models/User";
import {TableService} from "../../../services/TableService";
import {forEach} from "@angular/router/src/utils/collection";
import {merge, Observable} from "rxjs/";
import {FormControl} from "@angular/forms";



@Component({
  selector: 'users-table',
  templateUrl: './users-table.component.html',
  styleUrls: ['./users-table.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UsersTableComponent implements OnInit {
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  public dataSource: UsersTableCustomDatasource;
  currentTime=(new Date()).getTime();
  @Output() selectedUserID: EventEmitter<string> = new EventEmitter<string>();
  selectedRowIndex = '-1';
  displayedColumns = ['fullName', 'age', 'charm', 'totalBalance', 'maxBalance', 'minBalance'];

  filterText = new FormControl("");
  filterType = new FormControl("NAME");


  constructor(private httpService: HttpService){

  }
  onRowSelected(user) {
    this.selectedUserID.emit(user.id);
    console.log("Row clicked: ", user);
    this.selectedRowIndex = user.id;
  }
  public addOneRow(user){
    let tableModel=new TableModel();
      tableModel.id=user.id;
      tableModel.fullName=user.surname+" "+user.name+" "+user.patronymic;
      tableModel.charm=user.charm;
      tableModel.age=user.birthDate;
      tableModel.maxBalance=0;
      tableModel.minBalance=0;
      tableModel.totalBalance=0;
      this.dataSource.thisTable.push(tableModel);
      this.dataSource.tableSubject.next(this.dataSource.thisTable);
  }

  public updateOneRow(user){

      if(user===undefined){
        user.surname = '';
        user.name = '';
        user.patronymic= '';
        user.birthDate=0;
      }
    console.log(user);

      let id = -2;

      this.dataSource.thisTable.forEach((value, index) => {
        console.log("aasd");
        console.log(value.id);
        console.log(user.id);
        if (value.id===user.id) {
          id = index;
          console.log("Here duck comes");
        }
      });

      console.log(id);
      console.log(this.dataSource.thisTable);
      this.dataSource.thisTable[id].fullName=user.surname +" "+user.name+" "+user.patronymic;
      this.dataSource.thisTable[id].age= user.birthDate;
      let data:TableModel[]=[];
      this.dataSource.tableSubject.next(data);
      this.dataSource.tableSubject.next(this.dataSource.thisTable);


  }

  ngOnInit() {
    this.dataSource = new UsersTableCustomDatasource(this.tableFetcher(true));
    this.dataSource.loadTable();
  }

  ngAfterViewInit() {
    merge(this.sort.sortChange,this.paginator.page, this.filterText.valueChanges,this.filterType.valueChanges).pipe(
      tap(() => {
        this.dataSource = new UsersTableCustomDatasource(this.tableFetcher());
        this.dataSource.loadTable();
      })).subscribe();

  }
  tableFetcher(ngInit=false){
    let limit = 3;
    let skipNumber = 0;
    let sortDirection='asc';
    let sortType='fullname';
    let filterType = "NAME";
    let filterText = "";
    if(!ngInit) {
       limit = this.paginator.pageSize;
       skipNumber = this.paginator.pageIndex*this.paginator.pageSize ;
       sortDirection=this.sort.direction;
       sortType=this.sort.active;
       filterText = this.filterText.value.replace(/\s\s+/g, '');
       filterType = this.filterType.value;
    }

    console.log(skipNumber,limit,sortDirection,sortType);

    return this.httpService.get("/table/get-table-data", {
      skipNumber:skipNumber,
      limit: limit,
      sortDirection:sortDirection,
      sortType:sortType,
      filterType: filterType,
      filterText: filterText}
    );
  }

  loadTablePage() {
    this.dataSource.loadTable();
  }




}
