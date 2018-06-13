import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator, MatSort } from '@angular/material';
import { UsersTableDataSource } from './users-table-datasource';

@Component({
  selector: 'users-table',
  templateUrl: './users-table.component.html',
  styleUrls: ['./users-table.component.css']
})
export class UsersTableComponent implements OnInit {
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  dataSource: UsersTableDataSource;
  displayedColumns = ['fullName', 'age', 'charm', 'totalBalance', 'maxBalance', 'minBalance'];

  ngOnInit() {
    this.dataSource = new UsersTableDataSource(this.paginator, this.sort);
  }

  onRowSelected(row) {
    console.log("Row clicked: ", row);

  }
}
