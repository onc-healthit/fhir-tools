/**
 * Copyright 2018, Xyram Software Solutions. All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { FormControl, FormGroupDirective, NgForm, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { UtilityService } from './../shared/utility.service';

@Component({
  selector: 'app-config',
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.scss']
})
export class ConfigComponent implements OnInit {
  myForm: FormGroup;
  url: string;
  typeOfMode: string;

  constructor(public formBuilder: FormBuilder, private util: UtilityService) {
    this.myForm = new FormGroup({
      'urlFormControl': new FormControl('', [
        Validators.required
      ]),
      'mode': new FormControl('', [
        Validators.required
      ])
    });
  }

  ngOnInit() { }

  saveURL() {
    if (this.myForm.valid) {
      this.typeOfMode = this.myForm.get('mode').value;
      this.url = this.myForm.get('urlFormControl').value;
      this.util.fhirServerURL = this.myForm.get('urlFormControl').value;
      this.util.fhirmode = this.myForm.get('mode').value;
      this.util.notify(
        'FHIR Server URL Saved Successfully.',
        'success',
        'Saved'
      );
    } else {
      this.util.notify(
        'Please enter required fields.',
        'warn',
        'Warning'
      );
    }
  }

  tryme() {
    this.myForm.setValue({ 'mode': 'open', 'urlFormControl': 'https://fhirtest.sitenv.org/backend-app' });
    this.typeOfMode = this.myForm.get('mode').value;
    this.url = this.myForm.get('urlFormControl').value;
    this.util.fhirServerURL = this.myForm.get('urlFormControl').value;
    this.util.fhirmode = this.myForm.get('mode').value;
    // this.util.notify(
    //   'FHIR Server URL Saved Successfully.',
    //   'success',
    //   'Saved'
    // );
  }
}
