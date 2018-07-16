import {ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {MatPaginator, MatSort} from '@angular/material';
import { ClientRecordsCustomDatasource } from "./client-records-custom-datasource";
import { HttpService} from "../../../services/HttpService";
import {tap} from "rxjs/operators";
import {ClientRecord} from "../../../models/ClientRecord";
import {Client} from "../../../models/Client";
import {forEach} from "@angular/router/src/utils/collection";
import {merge, Observable} from "rxjs/";
import {FormControl} from "@angular/forms";



@Component({
  selector: 'client-records',
  templateUrl: './client-records.component.html',
  styleUrls: ['./client-records.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ClientRecordsComponent implements OnInit {
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  public dataSource: ClientRecordsCustomDatasource;
  currentTime=(new Date()).getTime();
  @Output() selectedClientId: EventEmitter<number> = new EventEmitter<number>();
  selectedRowIndex = '-1';
  displayedColumns = ['fullName', 'age', 'charm', 'totalBalance', 'maxBalance', 'minBalance'];

  filterText = new FormControl("");
  filterType = new FormControl("NAME");


  constructor(private httpService: HttpService){

  }
  onRowSelected(client) {
    this.selectedClientId.emit(client.id);
    console.log("Row clicked: ", client);
    this.selectedRowIndex = client.id;
  }
  public addOneRow(client){
    let clientRecord=new ClientRecord();
      clientRecord.id=client.id;
      clientRecord.fullName=client.surname+" "+client.name+" "+client.patronymic;
      clientRecord.charm=client.charm;
      clientRecord.age=client.birthDate;
      clientRecord.maxBalance=0;
      clientRecord.minBalance=0;
      clientRecord.totalBalance=0;
      this.dataSource.thisClientRecords.push(clientRecord);
      this.dataSource.clientRecordsSubject.next(this.dataSource.thisClientRecords);
  }

  public updateOneRow(client){

      if(client===undefined){
        client.surname = '';
        client.name = '';
        client.patronymic= '';
        client.birthDate=0;
      }
    console.log(client);

      let id = -2;

      this.dataSource.thisClientRecords.forEach((value, index) => {
        console.log("aasd");
        console.log(value.id);
        console.log(client.id);
        if (value.id===client.id) {
          id = index;
          console.log("Here duck comes");
        }
      });

      console.log(id);
      console.log(this.dataSource.thisClientRecords);

      this.dataSource.thisClientRecords[id].fullName=client.surname +" "+client.name+" "+client.patronymic;
      this.dataSource.thisClientRecords[id].age= client.birthDate;
      let data:ClientRecord[]=[];
      this.dataSource.clientRecordsSubject.next(data);
      this.dataSource.clientRecordsSubject.next(this.dataSource.thisClientRecords);


  }

  ngOnInit() {
    this.dataSource = new ClientRecordsCustomDatasource(this.clientRecordsFetcher(true));
    this.dataSource.loadClientRecords();
  }

  ngAfterViewInit() {
    merge(this.sort.sortChange,this.paginator.page, this.filterText.valueChanges,this.filterType.valueChanges).pipe(
      tap(() => {
        this.dataSource = new ClientRecordsCustomDatasource(this.clientRecordsFetcher());
        this.dataSource.loadClientRecords();
      })).subscribe();

  }
  clientRecordsFetcher(ngInit=false){
    let limit = 3;
    let skipNumber = 0;
    let sortDirection='asc';
    let sortType='FULLNAME';
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

    return this.httpService.get("/client-records/get-client-records", {
      skipNumber:skipNumber,
      limit: limit,
      sortDirection:sortDirection,
      sortType:sortType,
      filterType: filterType,
      filterText: filterText}
    );
  }

  loadClientRecordsPage() {
    this.dataSource.loadClientRecords();
  }


  getReport(reportType:string){
    let filename;
    this.httpService.post("/client-records/make-report", {
      'filterType':this.filterType.value,
      'filterText':this.filterText.value,
      'sortType':this.sort.active,
      'sortDirection':this.sort.direction,
      'reportType':reportType}).subscribe(
      response => {
        filename=response.json();
        let toPrint = (this.httpService.download("/client-records/download-report", {"filename":filename}).toString());
        console.log(toPrint);
        window.open(toPrint, "_blank");
      }
    );
  }







}
