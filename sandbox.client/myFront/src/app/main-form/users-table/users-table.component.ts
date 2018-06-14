import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, MatSort } from '@angular/material';
import { UsersTableDataSource } from './users-table-datasource';
import {UsersTableCustomDatasource} from "./users-table-custom-datasource";
import {TableService} from "../../../services/TableService";
import {HttpService} from "../../../services/HttpService";

@Component({
  selector: 'users-table',
  templateUrl: './users-table.component.html',
  styleUrls: ['./users-table.component.css']
})
export class UsersTableComponent implements OnInit {
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  dataSource: UsersTableCustomDatasource;

  displayedColumns = ['fullName', 'age', 'charm', 'totalBalance', 'maxBalance', 'minBalance'];


  constructor(private httpService: HttpService){

  }
  onRowSelected(row) {
    console.log("Row clicked: ", row);

  }

  ngOnInit() {
    this.dataSource = new UsersTableCustomDatasource(this.httpService);
    this.dataSource.loadTable(0);
  }
}
