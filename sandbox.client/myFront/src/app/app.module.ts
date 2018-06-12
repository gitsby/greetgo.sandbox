import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {HttpModule, JsonpModule} from "@angular/http";
import {FormsModule} from "@angular/forms";
import { AppComponent } from './app.component';
import {LoginComponent} from "./input/login.component";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { UsersTableComponent } from './main-form/users-table/users-table.component';
import {MainFormComponent} from "./main-form/main-form.component";
import { MatTableModule, MatPaginatorModule, MatSortModule } from '@angular/material';
import {HttpService} from "../services/HttpService";

@NgModule({
  declarations: [
    AppComponent,
    UsersTableComponent,
    LoginComponent,
    MainFormComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    HttpModule,
    JsonpModule,
    FormsModule
  ],
  providers: [HttpService],
  bootstrap: [AppComponent]
})
export class AppModule { }




