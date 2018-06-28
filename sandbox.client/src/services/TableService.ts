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

   getOneValue=(url)=> this.httpService.get(url).toPromise().then(
    response => response.text()
   );

  retrieveArrayOfData(url,params){
    return this.httpService.get(url,
      params);
  }
}
