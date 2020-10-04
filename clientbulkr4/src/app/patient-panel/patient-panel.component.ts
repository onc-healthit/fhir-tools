/**
 * Copyright 2018, Xyram Software Solutions. All rights reserved.
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import {
  MatPaginator,
  MatSort,
  MatDialog,
  MatDialogRef,
  MatTableDataSource
} from '@angular/material';
import { SelectionModel } from '@angular/cdk/collections';
import { AfterViewInit } from '@angular/core/src/metadata/lifecycle_hooks';
import { PatientPanelService } from './patient-panel.service';
import { UtilityService } from '../shared/utility.service';
import { GroupDialogComponent } from './group-dialog/group-dialog.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-patient-panel',
  templateUrl: './patient-panel.component.html',
  styleUrls: ['./patient-panel.component.scss']
})
export class PatientsPanelComponent implements OnInit, AfterViewInit {
  displayedColumns = ['select', 'id', 'name', 'gender', 'dob', 'address'];
  dataSource = new MatTableDataSource();
  selection = new SelectionModel(true, []);
  selectedPatients: any = [];
  patientsList: any;
  patientData: any;
  isLoadingResults = true;

  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private patientPanelService: PatientPanelService,
    private util: UtilityService,
    private dialog: MatDialog,
    private router: Router
  ) { }

  ngOnInit() {
    if (this.util.dsId) {
      console.log('IN If');
      this.loadPatients();
    } else {
      this.util.notify(
        'Please enter FHIR Server URL and try again',
        'warn',
        'FHIR Server URL'
      );
      this.router.navigate(['/config']);
    }

    // this.dataSource = new MatTableDataSource(ELEMENT_DATA);
  }

  ngAfterViewInit() {
    // this.dataSource.paginator = this.paginator;
  }

  /** Whether the number of selected elements matches the total number of rows. */
  isAllSelected() {
    this.selectedPatients = this.selection.selected;
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected()
      ? this.selection.clear()
      : this.dataSource.data.forEach(row => this.selection.select(row));
  }

  loadPatients() {
    this.patientPanelService
      .getListofAllPatients()
      .subscribe((response: Response) => {
        this.renderPatients(response.body);
      });
  }

  renderPatients(data) {
    this.patientsList = data.entry;
    this.patientData = [];
    for (let i = 0; i < this.patientsList.length; i++) {
      const patientObject = {};
      patientObject['id'] = this.patientsList[i].resource.id;
      patientObject['name'] =
        this.patientsList[i].resource.name[0].family[0] +
        ' ' +
        this.patientsList[i].resource.name[0].given[0];
      patientObject['gender'] = this.util.capitalizeFirstChar(
        this.patientsList[i].resource.gender
      );
      patientObject['dob'] = this.patientsList[i].resource.birthDate;
      patientObject['address'] =
        this.patientsList[i].resource.address[0].line[0] +
        ' ' +
        this.patientsList[i].resource.address[0].line[1] +
        ' ' +
        this.patientsList[i].resource.address[0].city +
        ' ' +
        this.patientsList[i].resource.address[0].state +
        ' ' +
        this.patientsList[i].resource.address[0].country +
        ' ' +
        this.patientsList[i].resource.address[0].postalCode;
      patientObject['start'] = this.patientsList[
        i
      ].resource.identifier[0].period.start;
      this.patientData.push(patientObject);
    }
    this.isLoadingResults = false;
    this.dataSource = new MatTableDataSource(this.patientData);
    this.dataSource.paginator = this.paginator;
  }

  groupName() {
    if (this.selectedPatients.length !== 0) {
      const dialogRef = this.dialog.open(GroupDialogComponent);
      dialogRef.afterClosed().subscribe(result => {
        const groupName = result;
        if (groupName !== '') {
          this.createPanel(groupName);
        }
      });
    } else {
      this.util.notify(
        'Select atleast one Patient to create Panel',
        'warn',
        'Warning'
      );
    }
  }
  createPanel(groupName: any) {
    const members = [];
    if (this.selectedPatients.length !== 0) {
      for (let j = 0; j < this.selectedPatients.length; j++) {
        const memberObj = {};
        const entityObj = {};
        const periodObj = {};
        entityObj['reference'] = 'Patient/' + this.selectedPatients[j].id;
        periodObj['start'] = this.selectedPatients[j].start;
        memberObj['entity'] = entityObj;
        memberObj['period'] = periodObj;
        members.push(memberObj);
      }
      this.patientPanelService
        .createGroups(members, groupName)
        .subscribe(res => {
          this.util.notify('Panel Created Successfully', 'success', 'Success');
        });
    } else {
      this.util.notify(
        'Select atleast one Patient to create Panel',
        'warn',
        'Warning'
      );
    }
  }
}
