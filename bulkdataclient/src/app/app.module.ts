import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpModule } from '@angular/http';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { AppComponent } from './app.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppRoutingModule } from './app-routing.module';
import { Ng2Bs3ModalModule } from 'ng2-bs3-modal/ng2-bs3-modal';

import { NavigationModule } from './navigation/navigation.module';
import { SharedModule } from './shared/shared.module';
import { BulkDataMaterialModule } from '../bulkdata-material.module';
import { HomeComponent } from './home/home.component';
import { ConfigComponent } from './config/config.component';
import { PatientsPanelComponent } from './patient-panel/patient-panel.component';
import { GroupDialogComponent } from './patient-panel/group-dialog/group-dialog.component';
import { ExtractComponent } from './extract/extract.component';
import { NdJsonDialogComponent } from './extract/ndjson-dialog/ndjson-dialog.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { SimpleNotificationsModule } from 'angular2-notifications';
import { RunTestComponent } from './run-test/run-test.component';
import { PatientPanelService } from './patient-panel/patient-panel.service';
import { ExtractService } from './extract/extract.service';
import { ConfigService } from './config/config.service';
import { RunTestService } from './run-test/run-test.service';
import { StepperComponent } from './run-test/stepper/stepper.component';
import { ConfigStepperComponent } from './config/config-stepper/config-stepper.component';

import * as moment from 'moment';

@NgModule({
  declarations: [
    AppComponent,
    PageNotFoundComponent,
    HomeComponent,
    RunTestComponent,
    ConfigComponent,
    PatientsPanelComponent,
    ExtractComponent,
    GroupDialogComponent,
    NdJsonDialogComponent,
    StepperComponent,
    ConfigStepperComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    SimpleNotificationsModule.forRoot(),
    HttpModule,
    HttpClientModule,
    SharedModule.forRoot(),
    NavigationModule,
    BulkDataMaterialModule,
    ReactiveFormsModule,
    FormsModule,
    Ng2Bs3ModalModule,
    AppRoutingModule
  ],
  exports: [GroupDialogComponent, NdJsonDialogComponent],
  entryComponents: [GroupDialogComponent, NdJsonDialogComponent],
  providers: [PatientPanelService, ExtractService, RunTestService, ConfigService],
  bootstrap: [AppComponent]
})
export class AppModule { }
