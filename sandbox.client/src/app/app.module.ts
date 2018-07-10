import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpModule, JsonpModule } from "@angular/http";
import { FormsModule, ReactiveFormsModule} from "@angular/forms";
import { AppComponent } from './app.component';
import { LoginComponent } from "./input/login.component";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { UsersTableComponent } from './main-form/users-table/users-table.component';
import { MainFormComponent } from "./main-form/main-form.component";
import {
  MatTableModule, MatInputModule,
  MatProgressBarModule, MatPaginatorModule,
  MatSortModule, MatButtonModule,
  MatButtonToggleModule, MatDialogModule, MatNativeDateModule
} from '@angular/material';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { HttpService } from "../services/HttpService";
import { UserDialogComponent } from './main-form/user-dialog/user-dialog.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {MatSelectModule} from '@angular/material/select';
// import { ControlsComponent } from './main-form/controls/controls.component';

@NgModule({
  declarations: [
    AppComponent,
    UsersTableComponent,
    LoginComponent,
    MainFormComponent,
    UserDialogComponent
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
  entryComponents: [UserDialogComponent]
})
export class AppModule { }




