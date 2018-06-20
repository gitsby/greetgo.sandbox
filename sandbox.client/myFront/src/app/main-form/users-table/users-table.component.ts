import {ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {MatPaginator, MatSort} from '@angular/material';
import { UsersTableCustomDatasource} from "./users-table-custom-datasource";
import { HttpService} from "../../../services/HttpService";
import {tap} from "rxjs/operators";
import {TableModel} from "../../../models/TableModel";
import {User} from "../../../models/User";
import {TableService} from "../../../services/TableService";
import {forEach} from "@angular/router/src/utils/collection";



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


  constructor(private tableService: TableService){

  }
  onRowSelected(user) {
    this.selectedUserID.emit(user.id);
    console.log("Row clicked: ", user);
    this.selectedRowIndex = user.id;
  }
  public addOneRow(user){
    let tableModel=new TableModel();
    let setId = (id) =>{

      tableModel.id=id;
      tableModel.fullName=user.surname+" "+user.name+" "+user.patronymic;
      tableModel.charm=user.charm;
      tableModel.age=user.birthDate;
      tableModel.maxBalance=0;
      tableModel.minBalance=0;
      tableModel.totalBalance=0;
      this.dataSource.thisTable.push(tableModel);
      this.dataSource.tableSubject.next(this.dataSource.thisTable);
    };

    this.dataSource.getLastId(setId);
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
  }

  ngOnInit() {
    this.dataSource = new UsersTableCustomDatasource(this.tableService);
    this.dataSource.loadTable();
  }

  ngAfterViewInit() {
    // this.dataSource.paginator
    this.sort.sortChange.pipe(
      tap(() => this.loadTablePage())
    )
      .subscribe();
    this.paginator.page.pipe(
      tap(() => this.loadTablePage())
    )
      .subscribe();
  }

  loadTablePage() {
    this.dataSource.loadTable(
      this.paginator.pageIndex,
      this.paginator.pageSize,
      this.sort.direction,
      this.sort.active);
  }



}
