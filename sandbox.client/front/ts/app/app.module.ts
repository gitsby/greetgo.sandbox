import {NgModule} from "@angular/core";
import {HttpModule, JsonpModule} from "@angular/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {RootComponent} from "./root.component";
import {LoginComponent} from "./input/login.component";
import {MainFormComponent} from "./main_form/main_form.component";
import {HttpService} from "./HttpService";
import {EditFormComponent} from "./edit_form/edit_form.component";
import {ListFormComponent} from "./list_form/list_form.component";
import {TextMaskModule} from "angular2-text-mask";

@NgModule({
    imports: [
        TextMaskModule,
        BrowserModule, HttpModule, JsonpModule, FormsModule,
        ReactiveFormsModule
    ],
    declarations: [
        RootComponent, LoginComponent, MainFormComponent, EditFormComponent,
        ListFormComponent
    ],
    bootstrap: [RootComponent],
    providers: [HttpService],
    entryComponents: [],
})
export class AppModule {
}