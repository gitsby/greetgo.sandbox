import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpModule, JsonpModule } from "@angular/http";
import { FormsModule, ReactiveFormsModule} from "@angular/forms";
import { AppComponent } from './app.component';
import { LoginComponent } from "./input/login.component";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ClientRecordsComponent } from './main-form/client-records/client-records.component';
import { MainFormComponent } from "./main-form/main-form.component";
import {
  MatTableModule, MatInputModule,
  MatProgressBarModule, MatPaginatorModule,
  MatSortModule, MatButtonModule,
  MatButtonToggleModule, MatDialogModule, MatNativeDateModule
} from '@angular/material';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { HttpService } from "../services/HttpService";
import { ClientDialogComponent } from './main-form/client-dialog/client-dialog.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {MatSelectModule} from '@angular/material/select';
// import { ControlsComponent } from './main-form/controls/controls.component';

@NgModule({
  declarations: [
    AppComponent,
    ClientRecordsComponent,
    LoginComponent,
    MainFormComponent,
    ClientDialogComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatProgressSpinnerModule,
    MatProgressBarModule,
    MatInputModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    HttpModule,
    JsonpModule,
    FormsModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatDialogModule,
    ReactiveFormsModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSelectModule,
  ],
  exports: [
    MatProgressSpinnerModule,
  ],
  providers: [HttpService],
  bootstrap: [AppComponent],
  entryComponents: [ClientDialogComponent]
})
export class AppModule { }




