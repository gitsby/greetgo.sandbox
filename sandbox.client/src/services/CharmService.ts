import { Injectable } from '@angular/core';
import {Charm} from "../models/Charm";
import {HttpService} from "./HttpService";

@Injectable()
export class CharmService {
  charms:Charm[];
  constructor(private httpService: HttpService) { }

  getCharms(){
    return this.httpService.get('/client-records/get-charms').subscribe(
      res =>{
        this.charms=res.json().data.map(charm=>Charm.copy(charm));

      }
    )
  }
  assignLocalCharms(callback){
    this.httpService.get('/client-records/get-charms').subscribe(
      res =>{
        this.charms=res.json().data.map(charm=>Charm.copy(charm));
        callback(this.charms);
      }
    )
  }
}
