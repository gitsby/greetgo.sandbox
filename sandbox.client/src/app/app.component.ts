import { Component } from '@angular/core';
import {HttpService} from "../services/HttpService";
import {AuthInfo} from "../models/AuthInfo";


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app';
  mode: string = "login";

  constructor(private httpService: HttpService) {}

  ngOnInit(): void {
    this.mode = 'init';
    this.startApp();
  }

  startApp() {
    if (!this.httpService.token) {
      this.mode = 'login';
      return;
    }

    this.httpService.get("/auth/info").toPromise().then(result => {
      let userInfo = result.json() as AuthInfo;
      if (userInfo.pageSize) this.httpService.pageSize = userInfo.pageSize;
      (<any>window).document.title = userInfo.appTitle;
      this.mode = 'main-form';
    }, error => {
      console.log(error);
      this.mode = "login";
    });

  }

  exit() {
    this.httpService.token = null;
    this.mode = 'login';
  }

}
