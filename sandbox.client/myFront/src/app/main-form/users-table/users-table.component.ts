import {Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {MatPaginator, MatSort} from '@angular/material';
import { UsersTableDataSource } from './users-table-datasource';
import { UsersTableCustomDatasource} from "./users-table-custom-datasource";
import { TableService} from "../../../services/TableService";
import { HttpService} from "../../../services/HttpService";
import {merge, mergeAll, tap} from "rxjs/operators";



@Component({
  selector: 'users-table',
  templateUrl: './users-table.component.html',
  styleUrls: ['./users-table.component.css']
})
export class UsersTableComponent implements OnInit {
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  dataSource: UsersTableCustomDatasource;
  currentTime=(new Date()).getTime();
  @Output() selectedUserID: EventEmitter<string> = new EventEmitter<string>();
  selectedRowIndex = '-1';
  displayedColumns = ['fullName', 'age', 'charm', 'totalBalance', 'maxBalance', 'minBalance'];


  constructor(private httpService: HttpService){

  }
  onRowSelected(user) {
    this.selectedUserID.emit(user.id);
    console.log("Row clicked: ", user);
    this.selectedRowIndex = user.id;
  }

  ngOnInit() {
    this.dataSource = new UsersTableCustomDatasource(this.httpService);
    this.dataSource.loadTable();
  }

  ngAfterViewInit() {
    // this.dataSource.paginator
    this.paginator.page.pipe(
          tap(() => this.loadTablePage())
      )
      .subscribe();
    this.sort.sortChange.pipe(
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
