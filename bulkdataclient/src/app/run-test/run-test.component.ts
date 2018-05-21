import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import {
  MatPaginator,
  MatSort,
  MatTableDataSource,
  MatDialog,
  MAT_DIALOG_DATA
} from '@angular/material';
import { RunTestService } from './run-test.service';
import { UtilityService } from '../shared/utility.service';
import { Router } from '@angular/router';
import { ISubscription } from 'rxjs/Subscription';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/interval';
import { FormGroup, Validators, FormControl } from '@angular/forms';
import { NdJsonDialogComponent } from '../extract/ndjson-dialog/ndjson-dialog.component';
import * as moment from 'moment';


@Component({
  selector: 'app-run-test',
  templateUrl: './run-test.component.html',
  styleUrls: ['./run-test.component.scss']
})
export class RunTestComponent implements OnInit {
  selectedOption: any;
  group: any;
  response: any;
  extractAllResponse: any;
  exportUsingParamsResponse: any;
  exportUsingGroupParamsResponse: any;
  findIndex: any;
  groupIDForm: FormGroup;
  paramsForm: FormGroup;
  groupParamsForm: FormGroup;
  id: any;
  location: any;
  extractList: any = [];
  extractData: any = [];
  extractAllData: any = [];
  extractParamsData: any = [];
  extractGroupParamsData: any = [];
  responseBody: any = {};
  subscribe: ISubscription;
  fhirserverurl: any;
  isLoadingResults = false;
  responseCode: any;
  extractAllResponseCode: any;
  exportUsingParamsCode: any;
  exportUsingGroupParamsCode: any;
  displayResponse = false;
  displayPOSTResponse = false;
  displayExtractAll = false;
  displayParamsResponse = false;
  displayGroupParamsResponse = false;
  postBody: any;
  exampleJSON: any;
  postcallResponseBody: any = {};
  exportAllContentLocation: any;
  extractByGroupContentLocation: any;
  exportUsingParamsContentLocation: any;
  exportUsingGroupParamsContentLocation: any;
  resourcesList: any = [];
  expandedPanel: any;
  secureFHIRServer: any;
  stepperexecuteAll: boolean;
  stepperexecuteGroupId: boolean;
  stepperexecuteparams: boolean;
  stepperexecuteGroupparams: boolean;

  extractAllUrl: any;
  exportByGroupIdUrl: any;
  exportUsingParamsUrl: any;
  exportGroupUsingParamsUrl: any;

  constructor(private runTestService: RunTestService,
    private util: UtilityService, private dialog: MatDialog,
    private router: Router) { }

  ngOnInit() {
    this.groupIDForm = new FormGroup({
      groupId: new FormControl('', Validators.required)
    });
    this.paramsForm = new FormGroup({
      sincedate: new FormControl('', Validators.required),
      resources: new FormControl('', Validators.required)
    });
    this.groupParamsForm = new FormGroup({
      groupId: new FormControl('', Validators.required),
      sincedate: new FormControl('', Validators.required),
      resources: new FormControl('', Validators.required)
    });
    this.checkBulkData();
    this.secureFHIRServer = 'https://fhirtest.sitenv.org/secure-fhir/fhir';
    this.fhirserverurl = 'https://fhirtest.sitenv.org/backend-app-secure';
    this.exportAllContentLocation = 'https://fhirtest.sitenv.org/backend-app-secure/bulkdata/121';
    this.extractByGroupContentLocation = 'https://fhirtest.sitenv.org/backend-app-secure/bulkdata/122';
    this.exportUsingParamsContentLocation = 'https://fhirtest.sitenv.org/backend-app-secure/bulkdata/123';
    this.exportUsingGroupParamsContentLocation = 'https://fhirtest.sitenv.org/backend-app-secure/bulkdata/124';
    this.resourcesList = ['AllergyIntolerance', 'CarePlan', 'Claim', 'Condition', 'Device', 'Diagnostic Report', 'DocumentReference', 'Encounter', 'Goals', 'Immunization', 'Location', 'Medication', 'MedicationAdministration', 'MedicationDispense', 'MedicationOrder', 'MedicationStatement', 'Observation', 'Organization', 'Practitioner', 'Procedure'];
    this.paramsForm.setValue({ 'resources': ['AllergyIntolerance', 'CarePlan'], 'sincedate': moment('1990-01-01') });
    this.groupParamsForm.setValue({ 'resources': ['AllergyIntolerance', 'CarePlan'], 'sincedate': moment('1990-01-01'), 'groupId': '27' });
  }
  getdetails() {
    if (this.groupIDForm.valid) {
      this.displayResponse = false;
      // this.isLoadingResults = true;
      this.id = this.groupIDForm.get('groupId').value;
      /*this.runTestService.getGroupById(this.id, this.fhirserverurl).subscribe((res: Response) => {
        const extractObject = {};
        extractObject['id'] = this.id;
        extractObject['contentlocation'] = res.headers.get('content-location');
        this.extractList.push(extractObject);
        this.location = res.headers
          .get('content-location')
          .split('/')
          .pop();
      });*/
    }
  }
  checkBulkData() {
    if (this.extractList.length > 0) {
      if (this.subscribe !== undefined && !this.subscribe.closed) {
        this.subscribe.unsubscribe();
      }
      this.subscribe = Observable.interval(5000).subscribe(x => {
        for (let m = 0; m < this.extractList.length; m++) {
          this.getBulkData(this.extractList[m]);
        }
        this.checkBulkData();
      });
    } else {
      if (this.subscribe !== undefined && !this.subscribe.closed) {
        this.subscribe.unsubscribe();
      }
      this.subscribe = Observable.interval(5000).subscribe(x => {
        this.checkBulkData();
      });
    }
  }
  getBulkData(extract: any) {
    this.runTestService
      .getBulkDataByContentLocation(extract.contentlocation)
      .subscribe((res: Response) => {
        if (res.status === 200) {
          if (this.expandedPanel === 'exportByGroupId') {
            this.displayResponse = true;
            this.isLoadingResults = false;
            this.responseCode = res.status;
            this.responseBody = res.body;
            this.response = this.responseBody;
            const result = this.responseBody.output;
            this.extractData = [];
            for (let p = 0; p < result.length; p++) {
              const dataObj = {
                resourceName: result[p].type,
                resourceLink: result[p].url
              };
              this.extractData.push(dataObj);
            }
          } else if (this.expandedPanel === 'exportWithParams') {
            this.displayParamsResponse = true;
            this.isLoadingResults = false;
            this.exportUsingParamsCode = res.status;
            this.responseBody = res.body;
            this.exportUsingParamsResponse = this.responseBody;
            const result = this.responseBody.output;
            this.extractParamsData = [];
            for (let p = 0; p < result.length; p++) {
              const dataObj = {
                resourceName: result[p].type,
                resourceLink: result[p].url
              };
              this.extractParamsData.push(dataObj);
            }
          } else if (this.expandedPanel === 'exportGroupWithParams') {
            this.displayGroupParamsResponse = true;
            this.isLoadingResults = false;
            this.exportUsingGroupParamsCode = res.status;
            this.responseBody = res.body;
            this.exportUsingGroupParamsResponse = this.responseBody;
            const result = this.responseBody.output;
            this.extractGroupParamsData = [];
            for (let p = 0; p < result.length; p++) {
              const dataObj = {
                resourceName: result[p].type,
                resourceLink: result[p].url
              };
              this.extractGroupParamsData.push(dataObj);
            }
          }
          this.extractList.pop(extract);
        }
      });
  }

  getSelectedNdJson(resourceName, resourceLink) {
    this.runTestService
      .getSelectedNdJson(resourceLink)
      .subscribe((res: Response) => {
        const config = {
          minWidth: 800,
          hasBackdrop: true,
          data: {
            ndjson: res.body,
            link: resourceLink,
            name: resourceName
          }
        };
        const dialogRef = this.dialog.open(NdJsonDialogComponent, config);
      });
  }

  exportAll() {
    this.isLoadingResults = true;
    // this.stepperexecute = true;
    this.runTestService.getBulkDataByContentLocation(this.exportAllContentLocation).subscribe((res: Response) => {
      this.stepperexecuteAll = true;
      this.displayExtractAll = true;
      this.isLoadingResults = false;
      this.extractAllResponseCode = res.status;
      this.responseBody = res.body;
      this.extractAllResponse = this.responseBody;
      this.extractAllUrl = 'https://fhirtest.sitenv.org/secure-fhir/fhir/Patient/$export';
      const result = this.responseBody.output;
      this.extractAllData = [];
      for (let p = 0; p < result.length; p++) {
        const dataObj = {
          resourceName: result[p].type,
          resourceLink: result[p].url
        };
        this.extractAllData.push(dataObj);
      }
    });
  }

  exportByGroupId() {
    this.isLoadingResults = true;

    this.runTestService.getBulkDataByContentLocation(this.extractByGroupContentLocation).subscribe((res: Response) => {
      this.displayResponse = true;
      this.stepperexecuteGroupId = true;
      this.isLoadingResults = false;
      this.responseCode = res.status;
      this.responseBody = res.body;
      this.response = this.responseBody;
      this.exportByGroupIdUrl = 'https://fhirtest.sitenv.org/secure-fhir/fhir/Group/1/$export';
      const result = this.responseBody.output;
      this.extractData = [];
      for (let p = 0; p < result.length; p++) {
        const dataObj = {
          resourceName: result[p].type,
          resourceLink: result[p].url
        };
        this.extractData.push(dataObj);
      }
    });
  }

  exportUsingParams() {
    // this.isLoadingResults = true;
    // if (this.paramsForm.valid) {
    //   const date = this.paramsForm.get('sincedate').value.format('YYYY-MM-DD');
    //   const resources = this.paramsForm.get('resources').value;
    //   const formURL = this.fhirserverurl + '/fhir/Patient/$export?_since=' + date + '&_type=' + resources.join(',');
    //   this.runTestService.exportUsingParams(formURL).subscribe((res: Response) => {
    //     const extractObject = {};
    //     extractObject['id'] = this.id;
    //     extractObject['contentlocation'] = res.headers.get('content-location');
    //     this.extractList.push(extractObject);
    //     this.location = res.headers
    //       .get('content-location')
    //       .split('/')
    //       .pop();
    //   });
    // }

    this.isLoadingResults = true;

    this.runTestService.getBulkDataByContentLocation(this.exportUsingParamsContentLocation).subscribe((res: Response) => {
      this.displayParamsResponse = true;
      this.stepperexecuteparams = true;
      this.isLoadingResults = false;
      this.exportUsingParamsCode = res.status;
      this.responseBody = res.body;
      this.exportUsingParamsResponse = this.responseBody;
      const result = this.responseBody.output;
      this.exportUsingParamsUrl = 'https://fhirtest.sitenv.org/secure-fhir/fhir/Patient/$export?_since=1990-01-01&_type=AllergyIntolerance,Condition';
      this.extractParamsData = [];
      for (let p = 0; p < result.length; p++) {
        const dataObj = {
          resourceName: result[p].type,
          resourceLink: result[p].url
        };
        this.extractParamsData.push(dataObj);
      }
    });
  }

  expandPanel(type) {
    this.expandedPanel = type;
  }

  exportGroupUsingParams() {
    // this.isLoadingResults = true;
    // if (this.groupParamsForm.valid) {
    //   const date = this.groupParamsForm.get('sincedate').value.format('YYYY-MM-DD');
    //   const resources = this.groupParamsForm.get('resources').value;
    //   const groupId = this.groupParamsForm.get('groupId').value;
    //   const formURL = this.fhirserverurl + '/fhir/Group/' + groupId + '/$export?_since=' + date + '&_type=' + resources.join(',');
    //   console.log(formURL);
    //   this.runTestService.exportUsingParams(formURL).subscribe((res: Response) => {
    //     const extractObject = {};
    //     extractObject['id'] = this.id;
    //     extractObject['contentlocation'] = res.headers.get('content-location');
    //     this.extractList.push(extractObject);
    //     this.location = res.headers
    //       .get('content-location')
    //       .split('/')
    //       .pop();
    //   });
    // }

    this.isLoadingResults = true;

    this.runTestService.getBulkDataByContentLocation(this.exportUsingGroupParamsContentLocation).subscribe((res: Response) => {
      this.displayGroupParamsResponse = true;
      this.stepperexecuteGroupparams = true;
      this.isLoadingResults = false;
      this.exportUsingGroupParamsCode = res.status;
      this.responseBody = res.body;
      this.exportUsingGroupParamsResponse = this.responseBody;
      const result = this.responseBody.output;
      this.exportGroupUsingParamsUrl = 'https://fhirtest.sitenv.org/secure/fhir/Group/1/$export?_since=1950-01-01&_type=AllergyIntolerance,Condition';
      this.extractGroupParamsData = [];
      for (let p = 0; p < result.length; p++) {
        const dataObj = {
          resourceName: result[p].type,
          resourceLink: result[p].url
        };
        this.extractGroupParamsData.push(dataObj);
      }
    });
  }

}
