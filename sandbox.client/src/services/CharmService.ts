import { Injectable } from '@angular/core';
import { HttpService } from 'HttpService';
import {Http} from "@angular/http";
import {Charm} from "../models/Charm";

@Injectable()
export class CharmService {
  httpService:HttpService = new HttpService(Http);
  charms:Charm[];
  constructor() { }

  getCharms(){
    return this.httpService.get('/client-records/get-charms').subscribe(
      res =>{
        this.charms=res.json().map(charm=>Charm.copy(charm));
      }
    )
  }
}
