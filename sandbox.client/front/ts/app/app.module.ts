import {NgModule} from "@angular/core";
import {HttpModule, JsonpModule} from "@angular/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {RootComponent} from "./root.component";
import {LoginComponent} from "./input/login.component";
import {MainFormComponent} from "./main_form/main-form.component";
import {HttpService} from "./HttpService";
import {ClientEditFormComponent} from "./edit_form/client-edit-form.component";
import {TextMaskModule} from "angular2-text-mask";
import {ClientListComponent} from "./list_form/client-list.component";
import {DatePipe} from "@angular/common";

@NgModule({
  imports: [
    TextMaskModule,
    BrowserModule,
    HttpModule,
    JsonpModule,
    FormsModule,
    ReactiveFormsModule
  ],
  declarations: [
    RootComponent,
    LoginComponent,
    MainFormComponent,
    ClientEditFormComponent,
    ClientListComponent
  ],
  bootstrap: [RootComponent],
  providers: [
    DatePipe,
    HttpService],
  entryComponents: [],
})
export class AppModule {
}