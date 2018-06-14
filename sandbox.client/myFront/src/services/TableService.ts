import {map} from "rxjs/operators";
import {Observable} from "rxjs/index";
import {TableModel} from "../models/TableModel";
import {Injectable} from "@angular/core";
import {HttpService} from "./HttpService";


@Injectable()
export class TableService{
  constructor(private httpService:HttpService){

  }


  retrieveTable(
    skipNumber:number,limit:number,
    sortDirection:string, sortType:string):Observable<TableModel[]> {
    let somethingGood = this.httpService.get("/table/get-table-data",
      {
        skipNumber: skipNumber, limit: limit,
        sortDirection: sortDirection, sortType: sortType
      })
      .pipe(map(res => res["payload"]));

    return somethingGood;
  }
}
