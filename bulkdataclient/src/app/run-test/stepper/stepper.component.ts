import { Component, OnInit, ViewChild, Input } from '@angular/core';
import {
  MatPaginator,
  MatSort,
  MatDialog,
  MatDialogRef,
  MatTableDataSource
} from '@angular/material';
import { SelectionModel } from '@angular/cdk/collections';
import { AfterViewInit } from '@angular/core/src/metadata/lifecycle_hooks';
import { UtilityService } from '../../shared/utility.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RunTestService } from '../run-test.service';
import { NdJsonDialogComponent } from '../../extract/ndjson-dialog/ndjson-dialog.component';


@Component({
  selector: 'app-stepper',
  templateUrl: './stepper.component.html',
  styleUrls: ['./stepper.component.scss']
})
export class StepperComponent implements OnInit {
  public now: Date = new Date();
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  expirydate: any;
  respBody: any;
  respBodyJSON: any;
  @Input() myData;
  @Input() jsonview;
  @Input() url;

  constructor(private formBuilder: FormBuilder, private runTestService: RunTestService, private dialog: MatDialog) {
    // console.log(this.myData);
    this.respBody = '{"expires_in":"900","token_type":"bearer","access_token":"a74a10835af12af436c843495912aa79"}';
    this.respBodyJSON = JSON.parse(this.respBody);

  }
  ngOnInit() {
    this.firstFormGroup = this.formBuilder.group({
      firstCtrl: ['', Validators.required]
    });
    this.secondFormGroup = this.formBuilder.group({
      secondCtrl: ['', Validators.required]
    });
    this.expirydate = new Date();
    this.expirydate.setDate(this.expirydate.getDate() + 10);
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
}
