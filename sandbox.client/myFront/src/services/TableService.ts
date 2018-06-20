import {map} from "rxjs/operators";
import {Observable} from "rxjs/index";
import {TableModel} from "../models/TableModel";
import {Injectable} from "@angular/core";
import {HttpService} from "./HttpService";
import {Http} from "@angular/http";
import {HttpParams} from "@angular/common/http";


@Injectable()
export class TableService{
  constructor(private httpService:HttpService, private http: Http){

  }


  retrieveTable(
    skipNumber:number,limit:number,
    sortDirection:string, sortType:string):Observable<TableModel[]> {
    console.log(skipNumber, limit, sortDirection, sortType);

    // let somethingBad= this.http.get("/table/get-table-data",params:{
    //    new HttpParams()
    //     .set('skipNumber', skipNumber.toString)
    //     .set('limit', limit.toString())
    //     .set('sortDirection', sortDirection.toString())
    //     .set('sortType', sortType.toString())
    //   });
    // // somethingBad.subscribe(response => console.log(response.text()));
    // return somethingBad.pipe(map(res => res["payload"]));


    let somethingGood = this.httpService.get("/table/get-table-data",
      {
        skipNumber: skipNumber, limit: limit,
        sortDirection: sortDirection, sortType: sortType
      });
    somethingGood.subscribe(response => console.log(response.text()));
    return somethingGood.pipe(map(res => res["payload"]));

  }
}
