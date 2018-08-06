import { ObserversModule } from '@angular/cdk/observers';
import { Observable } from 'rxjs/Observable';
import { ISubscription } from 'rxjs/Subscription';
/**
 * Copyright 2018, Xyram Software Solutions. All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import {
  FormControl,
  Validators,
  FormGroup,
  FormBuilder,
  ReactiveFormsModule,
  FormsModule
} from '@angular/forms';
import { UtilityService } from './../shared/utility.service';
import { ConfigService } from './config.service';
import { environment } from '../../environments/environment';
import * as moment from 'moment';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Subject } from 'rxjs/Subject';
import { CHECKBOX_REQUIRED_VALIDATOR } from '@angular/forms/src/directives/validators';

@Component({
  selector: 'app-config',
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.scss']
})
export class ConfigComponent implements OnInit {
  myForm: FormGroup;
  groupIDform: FormGroup;
  paramsform: FormGroup;
  groupparamsform: FormGroup;
  url: string;
  typeOfMode: string;
  openMode = true;
  secureMode = false;
  scopesList: any = [];
  resourcesList: any = [];
  extractAllData: any = [];
  selectedFile: any;
  selectedFileName: any;
  fd: any;
  apiBaseUrl: any;
  location: any;
  extractList: any = [];
  extractData: any = [];
  subscribe: ISubscription;
  responseBody: any = {};
  origin: string;
  secureServerSelected = false;
  openServerSaved = false;
  secureServerSaved = false;
  serverDetailsSaved = false;
  isClickedOpen: boolean;
  isClickedsecure: boolean;
  displayExtractAll: boolean;
  BaseURL: any;
  groupid: any;
  groupid2: any;
  date1: any;
  date2: any;
  resources1: any;
  resources2: any;
  extractAllResponse: any;
  exportAllContentLocation: any;
  extractAllResponseCode: any;
  stepperData: any = {};
  fhirURL: string;
  extractionByGroupId = false;

  enableExportAllStepper = false;
  enableGroupByIdStepper = false;
  enableDateandResourceStepper = false;
  enableGroupDateandResourceStepper = false;
  extractType: string;
  isLoadingResults: boolean;
  _subscription: ISubscription;
  minDate = new Date(1950, 1, 1);
  maxDate = new Date();

  siteServersList = [];

  constructor(
    public formBuilder: FormBuilder,
    private util: UtilityService,
    private configService: ConfigService
  ) {
    this.myForm = new FormGroup({
      openurlFormControl: new FormControl('', [Validators.required]),
      mode: new FormControl('', [Validators.required]),
      secureurlFormControl: new FormControl('', [Validators.required]),
      issuerURI: new FormControl('', [Validators.required]),
      clientId: new FormControl('', [Validators.required]),
      tokenURL: new FormControl('', [Validators.required]),
      privateKey: new FormControl('', [Validators.required]),
      scopes: new FormControl('', [Validators.required])
    });

    this.isLoadingResults = this.configService.isLoadingResults;
    this._subscription = this.configService.loading.subscribe(value => {
      console.log('In Subscription');
      this.isLoadingResults = value;
    });
  }

  ngOnInit() {
    this.scopesList = ['system/*.read', 'user/*.read'];
    this.apiBaseUrl = 'https://fhirprod.sitenv.org/backend-app-secure';
    this.origin = location.origin;
    this.resourcesList = [
      'AllergyIntolerance',
      'CarePlan',
      'Condition',
      'Device',
      'DiagnosticReport',
      'DocumentReference',
      'Goal',
      'Immunization',
      'Location',
      'Medication',
      'MedicationAdministration',
      'MedicationDispense',
      'MedicationOrder',
      'MedicationStatement',
      'Observation',
      'Organization',
      'Patient',
      'Procedure'
    ];
    this.siteServersList = [
      'https://fhirprod.sitenv.org/open-fhir/fhir',
      'https://fhirprod.sitenv.org/secure-fhir/fhir',
      'https://fhirprod.sitenv.org/open-fhir/fhir',
      'https://fhirprod.sitenv.org/secure-fhir/fhir',
      'https://fhirprod.sitenv.org/fhir-backend/fhir',
      'https://fhirprod.sitenv.org/fhir-backend/fhir'
    ];
    this.checkBulkData();
  }

  saveURL() {
    this.typeOfMode = this.myForm.get('mode').value;
    if (this.typeOfMode === 'open') {
      // this.checkurl(this.myForm.get('openurlFormControl').value);
      this.fhirURL = this.myForm.get('openurlFormControl').value;
      const siteURL = this.siteServersList.filter(x => {
        return x === this.fhirURL;
      });
      const postObj = {};
      postObj['fhirURL'] = this.myForm.get('openurlFormControl').value;
      postObj['iss'] = '';
      postObj['sub'] = '';
      postObj['aud'] = '';
      postObj['scope'] = '';
      postObj['isSecure'] = false;
      this.fd = new FormData();
      this.fd.append('ds', JSON.stringify(postObj));

      if (siteURL.length > 0) {
        this.configService
          .saveOpenConfig(this.fd)
          .subscribe((response: Response) => {
            this.openServerSaved = true;
            this.secureServerSaved = false;
            this.serverDetailsSaved = true;
            this.responseBody = response.body;
            this.util.dsId = this.responseBody.dsId;
            this.util.notify(
              'FHIR Server URL Saved Successfully.',
              'success',
              'Saved'
            );
          });
      } else {
        this.configService.validateURL(this.fhirURL).subscribe(res => {
          console.log(res);
          this.configService
            .saveOpenConfig(this.fd)
            .subscribe((response: Response) => {
              this.openServerSaved = true;
              this.secureServerSaved = false;
              this.serverDetailsSaved = true;
              this.responseBody = response.body;
              this.util.dsId = this.responseBody.dsId;
              this.util.notify(
                'FHIR Server URL Saved Successfully.',
                'success',
                'Saved'
              );
            });
        });
      }
    } else if (this.typeOfMode === 'secure') {
      this.fhirURL = this.myForm.get('secureurlFormControl').value;
      const postObj = {};
      postObj['fhirURL'] = this.myForm.get('secureurlFormControl').value;
      postObj['iss'] = this.myForm.get('issuerURI').value;
      postObj['sub'] = this.myForm.get('clientId').value;
      postObj['aud'] = this.myForm.get('tokenURL').value;
      postObj['scope'] = this.myForm.get('scopes').value.join(',');
      postObj['isSecure'] = true;
      this.fd = new FormData();
      this.fd.append('ds', JSON.stringify(postObj));
      this.fd.append('file', this.selectedFile);

      const siteURL = this.siteServersList.filter(x => {
        return x === this.fhirURL;
      });

      if (siteURL.length > 0) {
        this.configService
          .saveSecureConfig(this.fd)
          .subscribe((response: Response) => {
            this.openServerSaved = false;
            this.secureServerSaved = true;
            this.serverDetailsSaved = true;
            this.responseBody = response.body;
            this.util.dsId = this.responseBody.dsId;
            this.util.notify(
              'FHIR Server URL Saved Successfully.',
              'success',
              'Saved'
            );
          });
      } else {
        this.configService.validateURL(this.fhirURL).subscribe(res => {
          console.log(res);
          this.configService
            .saveSecureConfig(this.fd)
            .subscribe((response: Response) => {
              this.openServerSaved = false;
              this.secureServerSaved = true;
              this.serverDetailsSaved = true;
              this.responseBody = response.body;
              this.util.dsId = this.responseBody.dsId;
              this.util.notify(
                'FHIR Server URL Saved Successfully.',
                'success',
                'Saved'
              );
            });
        });
      }
    }
  }

  // checkurl(url) {
  //   console.log('in check url');
  //   this.checkmetadata(url);
  // }

  // checkmetadata(url) {
  //   this.configService.checkmetadata(url).subscribe(res => {
  //     console.log(res);
  //   });
  // }

  tryme(mode) {
    if (mode === 'open') {
      this.fhirURL = 'https://fhirprod.sitenv.org/open-fhir/fhir';
      this.openMode = true;
      this.secureMode = false;
      this.secureServerSelected = false;
      this.openServerSaved = false;
      this.secureServerSaved = false;
      this.serverDetailsSaved = false;
      this.isClickedOpen = true;
      this.isClickedsecure = false;
      this.myForm.setValue({
        mode: 'open',
        openurlFormControl: 'https://fhirprod.sitenv.org/open-fhir/fhir',
        secureurlFormControl: '',
        issuerURI: '',
        clientId: '',
        tokenURL: '',
        privateKey: '',
        scopes: []
      });
      this.typeOfMode = this.myForm.get('mode').value;
      this.url = this.myForm.get('openurlFormControl').value;
      this.util.fhirServerURL = this.myForm.get('openurlFormControl').value;
      this.util.fhirmode = this.myForm.get('mode').value;
      window.scrollTo(0, 0);
    } else if (mode === 'secure') {
      this.openMode = false;
      this.secureMode = true;
      this.openServerSaved = false;
      this.secureServerSaved = false;
      this.secureServerSelected = true;
      this.serverDetailsSaved = false;
      this.isClickedsecure = true;
      this.isClickedOpen = false;
      this.myForm.setValue({
        mode: 'secure',
        openurlFormControl: '',
        secureurlFormControl: '',
        issuerURI: '',
        clientId: '',
        tokenURL: '',
        privateKey: '',
        scopes: []
      });
      this.typeOfMode = this.myForm.get('mode').value;
      this.url = this.myForm.get('secureurlFormControl').value;
      this.util.fhirServerURL = this.myForm.get('secureurlFormControl').value;
      this.util.fhirmode = this.myForm.get('mode').value;
    }

    // this.openMode = true;
    // this.secureMode = false;
    // this.myForm.setValue({ 'mode': 'open', 'openurlFormControl': 'https://fhirprod.sitenv.org/open-fhir/fhir', 'secureurlFormControl': '', 'issuerURI': '', 'clientId': '', 'tokenURL': '', 'privateKey': '', 'scopes': [] });
    // this.typeOfMode = this.myForm.get('mode').value;
    // this.url = this.myForm.get('openurlFormControl').value;
    // this.util.fhirServerURL = this.myForm.get('openurlFormControl').value;
    // this.util.fhirmode = this.myForm.get('mode').value;
  }

  loadPrivateKey(file) {
    this.selectedFile = file.target.files[0];
    this.selectedFileName = file.target.files[0].name;
  }

  changeMode(ev) {
    const mode = ev.value;
    if (mode === 'open') {
      this.openMode = true;
      this.secureMode = false;
    } else if (mode === 'secure') {
      this.openMode = false;
      this.secureMode = true;
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
    this.configService
      .getBulkDataByContentLocation(extract.contentlocation)
      .subscribe((res: Response) => {
        const getPollData = this.stepperData.getPollData;
        getPollData['getApiUrl'] = extract.contentlocation;
        if (res.status === 202 && res.headers.get('X-Progress')) {
          const getPollAcceptedResponseHeaders = {};
          console.log(res.headers);
          getPollAcceptedResponseHeaders['allowMethods'] = res.headers.get(
            'Access-Control-Allow-Methods'
          );
          getPollAcceptedResponseHeaders['allowOrigin'] = res.headers.get(
            'Access-Control-Allow-Origin'
          );
          getPollAcceptedResponseHeaders['maxAge'] = res.headers.get(
            'Access-Control-Max-Age'
          );
          getPollAcceptedResponseHeaders['contentType'] = res.headers.get(
            'Content-Type'
          );
          getPollAcceptedResponseHeaders['xProgress'] = res.headers.get(
            'X-Progress'
          );
          getPollData[
            'acceptedResponseHeaders'
          ] = getPollAcceptedResponseHeaders;
          this.stepperData['getPollData'] = getPollData;
          console.log('in inprogress');
        } else if (res.status === 200) {
          const getPollCompletedResponseHeaders = {};
          console.log(res.headers);
          getPollCompletedResponseHeaders['allowMethods'] = res.headers.get(
            'Access-Control-Allow-Methods'
          );
          getPollCompletedResponseHeaders['allowOrigin'] = res.headers.get(
            'Access-Control-Allow-Origin'
          );
          getPollCompletedResponseHeaders['maxAge'] = res.headers.get(
            'Access-Control-Max-Age'
          );
          getPollCompletedResponseHeaders['contentType'] = res.headers.get(
            'Content-Type'
          );
          getPollCompletedResponseHeaders['link'] = res.headers.get('Link');
          getPollData[
            'completedResponseHeaders'
          ] = getPollCompletedResponseHeaders;
          this.stepperData['getPollData'] = getPollData;
          this.responseBody = res.body;
          // if (this.responseBody.output.length > 0) {
          //   for (let i = 0; i < this.responseBody.output; i++) {
          //     this.responseBody.output[i].type = this.util.capitalizeFirstChar(this.responseBody.output[i].type);
          //   }
          // }
          this.responseBody = this.responseBody.output.filter(x => {
            return (x.type = this.util.capitalizeFirstChar(x.type));
          });
          console.log(this.responseBody);
          this.stepperData['getDownloadData'] = this.responseBody;
          this.stepperData['enableStepper'] = true;
          // this.isLoadingResults = false;
          if (this.extractType === 'exportAll') {
            this.enableExportAllStepper = true;
          } else if (this.extractType === 'extractByGroupId') {
            this.enableGroupByIdStepper = true;
          } else if (this.extractType === 'exportByDateandResource') {
            this.enableDateandResourceStepper = true;
          } else if (this.extractType === 'exportByGroupDateandResource') {
            this.enableGroupDateandResourceStepper = true;
          }

          // const result = this.responseBody.output;
          // const exportLinks = res.headers.get('Link');
          // const links = exportLinks.split(',');
          // this.extractData = [];
          // for (let p = 0; p < result.length; p++) {
          //   const dataObj = {
          //     resourceName: this.util.capitalizeFirstChar(result[p].type),
          //     resourceLink: result[p].url
          //   };
          //   this.extractData.push(dataObj);
          // }
          this.extractList.pop(extract);
        }
      });
  }

  exportAll() {
    // this.isLoadingResults = true;
    this.enableExportAllStepper = false;
    const url = this.apiBaseUrl + '/fhir/Patient/$export';
    console.log(url);
    this.configService.exportAll().subscribe((res: Response) => {
      console.log(res);
      this.extractType = 'exportAll';
      const extractObject = {};
      extractObject['groupId'] = this.groupid;
      extractObject['contentlocation'] = res.headers.get('content-location');
      this.extractList.push(extractObject);
      const getApiData = {};
      const getApiResponseHeaders = {};
      console.log(res.headers);
      getApiResponseHeaders['allowMethods'] = res.headers.get(
        'Access-Control-Allow-Methods'
      );
      getApiResponseHeaders['allowOrigin'] = res.headers.get(
        'Access-Control-Allow-Origin'
      );
      getApiResponseHeaders['maxAge'] = res.headers.get(
        'Access-Control-Max-Age'
      );
      getApiResponseHeaders['contentLocation'] = res.headers.get(
        'Content-Location'
      );
      getApiResponseHeaders['contentType'] = res.headers.get('Content-Type');
      getApiResponseHeaders['expires'] = res.headers.get('Expires');
      getApiData['responseHeaders'] = getApiResponseHeaders;
      getApiData['getApiUrl'] = this.fhirURL + '/Patient/$export';
      this.stepperData['getApiData'] = getApiData;
      this.stepperData['getPollData'] = {};
    });
  }

  extractByGroupId() {
    // this.isLoadingResults = true;
    this.enableGroupByIdStepper = false;
    console.log('In Group By id function');
    const groupId = this.groupid;
    this.configService.getGroupById(groupId).subscribe((res: Response) => {
      this.extractType = 'extractByGroupId';
      console.log(res);
      const extractObject = {};
      extractObject['groupId'] = this.groupid;
      extractObject['contentlocation'] = res.headers.get('content-location');
      this.extractList.push(extractObject);
      const getApiData = {};
      const getApiResponseHeaders = {};
      console.log(res.headers);
      getApiResponseHeaders['allowMethods'] = res.headers.get(
        'Access-Control-Allow-Methods'
      );
      getApiResponseHeaders['allowOrigin'] = res.headers.get(
        'Access-Control-Allow-Origin'
      );
      getApiResponseHeaders['maxAge'] = res.headers.get(
        'Access-Control-Max-Age'
      );
      getApiResponseHeaders['contentLocation'] = res.headers.get(
        'Content-Location'
      );
      getApiResponseHeaders['contentType'] = res.headers.get('Content-Type');
      getApiResponseHeaders['expires'] = res.headers.get('Expires');
      getApiData['responseHeaders'] = getApiResponseHeaders;
      getApiData['getApiUrl'] =
        this.fhirURL + '/Group/' + this.groupid + '/$export';
      this.stepperData['getApiData'] = getApiData;
      this.stepperData['getPollData'] = {};
    });
  }

  exportByDateandResource() {
    // this.isLoadingResults = true;
    this.enableDateandResourceStepper = false;
    const date = moment(this.date1).format('YYYY-MM-DD');
    const resources = this.resources1;
    console.log(date + '------' + resources);
    this.configService
      .getByDateandResources(date, resources)
      .subscribe((res: Response) => {
        this.extractType = 'exportByDateandResource';
        console.log(res);
        const extractObject = {};
        extractObject['groupId'] = this.groupid;
        extractObject['contentlocation'] = res.headers.get('content-location');
        this.extractList.push(extractObject);
        const getApiData = {};
        const getApiResponseHeaders = {};
        console.log(res.headers);
        getApiResponseHeaders['allowMethods'] = res.headers.get(
          'Access-Control-Allow-Methods'
        );
        getApiResponseHeaders['allowOrigin'] = res.headers.get(
          'Access-Control-Allow-Origin'
        );
        getApiResponseHeaders['maxAge'] = res.headers.get(
          'Access-Control-Max-Age'
        );
        getApiResponseHeaders['contentLocation'] = res.headers.get(
          'Content-Location'
        );
        getApiResponseHeaders['contentType'] = res.headers.get('Content-Type');
        getApiResponseHeaders['expires'] = res.headers.get('Expires');
        getApiData['responseHeaders'] = getApiResponseHeaders;
        getApiData['getApiUrl'] =
          this.fhirURL +
          '/Patient/$export?_since=' +
          date +
          '&_type=' +
          resources;
        this.stepperData['getApiData'] = getApiData;
        this.stepperData['getPollData'] = {};
      });
  }

  exportByGroupDateandResource() {
    // this.isLoadingResults = true;
    this.enableGroupDateandResourceStepper = false;
    const groupId = this.groupid2;
    const date = moment(this.date2).format('YYYY-MM-DD');
    const resources = this.resources2;
    console.log(date + '------' + resources);

    this.configService
      .getByGroupDateandResources(groupId, date, resources)
      .subscribe((res: Response) => {
        this.extractType = 'exportByGroupDateandResource';
        console.log(res);
        const extractObject = {};
        extractObject['groupId'] = this.groupid;
        extractObject['contentlocation'] = res.headers.get('content-location');
        this.extractList.push(extractObject);
        const getApiData = {};
        const getApiResponseHeaders = {};
        console.log(res.headers);
        getApiResponseHeaders['allowMethods'] = res.headers.get(
          'Access-Control-Allow-Methods'
        );
        getApiResponseHeaders['allowOrigin'] = res.headers.get(
          'Access-Control-Allow-Origin'
        );
        getApiResponseHeaders['maxAge'] = res.headers.get(
          'Access-Control-Max-Age'
        );
        getApiResponseHeaders['contentLocation'] = res.headers.get(
          'Content-Location'
        );
        getApiResponseHeaders['contentType'] = res.headers.get('Content-Type');
        getApiResponseHeaders['expires'] = res.headers.get('Expires');
        getApiData['responseHeaders'] = getApiResponseHeaders;
        getApiData['getApiUrl'] =
          this.fhirURL +
          '/Group/' +
          groupId +
          '/$export?_since=' +
          date +
          '&_type=' +
          resources;
        this.stepperData['getApiData'] = getApiData;
        this.stepperData['getPollData'] = {};
      });
  }
}
