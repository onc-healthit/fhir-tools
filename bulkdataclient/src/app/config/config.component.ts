/**
 * Copyright 2018, Xyram Software Solutions. All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { FormControl, FormGroupDirective, NgForm, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { UtilityService } from './../shared/utility.service';
import { ConfigService } from './config.service';

@Component({
  selector: 'app-config',
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.scss']
})
export class ConfigComponent implements OnInit {
  myForm: FormGroup;
  url: string;
  typeOfMode: string;
  openMode = true;
  secureMode = false;
  scopesList: any = [];
  selectedFile: any;
  selectedFileName: any;
  fd: any;
  responseBody: any = {};

  constructor(public formBuilder: FormBuilder, private util: UtilityService, private configService: ConfigService) {
    this.myForm = new FormGroup({
      'openurlFormControl': new FormControl('', [
        Validators.required
      ]),
      'mode': new FormControl('', [
        Validators.required
      ]),
      'secureurlFormControl': new FormControl('', [
        Validators.required
      ]),
      'issuerURI': new FormControl('', [
        Validators.required
      ]),
      'clientId': new FormControl('', [
        Validators.required
      ]),
      'tokenURL': new FormControl('', [
        Validators.required
      ]),
      'privateKey': new FormControl('', [
        Validators.required
      ]),
      'scopes': new FormControl('', [
        Validators.required
      ])
    });
  }

  ngOnInit() {
    this.scopesList = ['patient/*.read', 'user/*.read'];
  }

  saveURL() {
    this.typeOfMode = this.myForm.get('mode').value;
    if (this.typeOfMode === 'open') {
      const postObj = {};
      postObj['fhirURL'] = this.myForm.get('openurlFormControl').value;
      postObj['iss'] = '';
      postObj['sub'] = '';
      postObj['aud'] = '';
      postObj['scope'] = '';
      postObj['isSecure'] = false;
      this.fd = new FormData();
      this.fd.append('ds', JSON.stringify(postObj));

      this.configService.saveOpenConfig(this.fd).subscribe((res: Response) => {
        this.responseBody = res.body;
        this.util.dsId = this.responseBody.dsId;
        this.util.notify(
          'FHIR Server URL Saved Successfully.',
          'success',
          'Saved'
        );
      });
    } else if (this.typeOfMode === 'secure') {
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

      this.configService.saveSecureConfig(this.fd).subscribe((res: Response) => {
        this.responseBody = res.body;
        this.util.dsId = this.responseBody.dsId;
        this.util.notify(
          'FHIR Server URL Saved Successfully.',
          'success',
          'Saved'
        );
      });
    }
  }

  tryme() {
    // if (mode === 'open') {
    //   this.openMode = true;
    //   this.secureMode = false;
    //   this.myForm.setValue({ 'mode': 'open', 'openurlFormControl': 'https://fhirtest.sitenv.org/open-fhir/fhir', 'secureurlFormControl': '', 'issuerURI': '', 'clientId': '', 'tokenURL': '', 'privateKey': '', 'scopes': [] });
    //   this.typeOfMode = this.myForm.get('mode').value;
    //   this.url = this.myForm.get('openurlFormControl').value;
    //   this.util.fhirServerURL = this.myForm.get('openurlFormControl').value;
    //   this.util.fhirmode = this.myForm.get('mode').value;
    // } else if (mode === 'secure') {
    //   this.openMode = false;
    //   this.secureMode = true;
    // }

    this.openMode = true;
    this.secureMode = false;
    this.myForm.setValue({ 'mode': 'open', 'openurlFormControl': 'https://fhirtest.sitenv.org/open-fhir/fhir', 'secureurlFormControl': '', 'issuerURI': '', 'clientId': '', 'tokenURL': '', 'privateKey': '', 'scopes': [] });
    this.typeOfMode = this.myForm.get('mode').value;
    this.url = this.myForm.get('openurlFormControl').value;
    this.util.fhirServerURL = this.myForm.get('openurlFormControl').value;
    this.util.fhirmode = this.myForm.get('mode').value;
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
}
