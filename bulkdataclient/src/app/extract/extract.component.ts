/**
 * Copyright 2018, Xyram Software Solutions. All rights reserved.
 */
import { Component, OnInit, ViewChild } from '@angular/core';
import {
  MatPaginator,
  MatSort,
  MatTableDataSource,
  MatDialog,
  MAT_DIALOG_DATA
} from '@angular/material';
import { SelectionModel } from '@angular/cdk/collections';
import { AfterViewInit } from '@angular/core/src/metadata/lifecycle_hooks';
import { UtilityService } from '../shared/utility.service';
import { ExtractService } from './extract.service';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/interval';
import { ISubscription } from 'rxjs/Subscription';

@Component({
  selector: 'app-extract',
  templateUrl: './extract.component.html',
  styleUrls: ['./extract.component.scss']
})
export class ExtractComponent implements OnInit {
  displayedColumns = ['id', 'name', 'patientList', 'status', 'actions', 'data'];
  dataSource = new MatTableDataSource();
  groupsList: any;
  group: any;
  groupTableData: any;
  extractList: any = [];
  extractData: any = [];
  subscribe: ISubscription;
  isLoadingResults = true;
  config = {
    hasBackdrop: true,
    data: []
  };

  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private util: UtilityService,
    private extractsService: ExtractService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.loadGroups();
    this.checkBulkData();
  }
  loadGroups() {
    this.extractsService
      .getListofAllGroups()
      .subscribe((response: Response) => {
        this.renderGroups(response.body);
      });
  }

  renderGroups(data) {
    this.groupsList = data.entry;
    this.groupTableData = [];
    for (let i = 0; i < this.groupsList.length; i++) {
      this.group = this.groupsList[i].resource;
      const groupObject = {};
      groupObject['id'] = this.group.id;
      const members = this.group.member;
      const patList = [];
      for (let j = 0; j < members.length; j++) {
        const patId = members[j].entity.reference.split('/')[1];
        patList.push(patId);
      }
      groupObject['name'] = this.group.name;
      groupObject['patientList'] = patList.join(',');
      groupObject['status'] = '';
      groupObject['data'] = this.extractData;
      groupObject['exported'] = false;
      this.groupTableData.push(groupObject);
    }
    this.isLoadingResults = false;
    this.dataSource = new MatTableDataSource(this.groupTableData);
    this.dataSource.paginator = this.paginator;
  }

  extractByGroup(group: any) {
    this.extractsService.getGroupById(group.id).subscribe((res: Response) => {
      const extractObject = {};
      extractObject['groupId'] = group.id;
      extractObject['contentlocation'] = res.headers.get('content-location');
      this.extractList.push(extractObject);
      const groupIndex = this.groupTableData.findIndex(x => x === group);
      this.groupTableData[groupIndex].exported = true;
    });
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
    this.extractsService
      .getBulkDataByContentLocation(extract.contentlocation)
      .subscribe((res: Response) => {
        if (res.status === 202 && res.headers.get('X-Progress')) {
          const groupIndex = this.groupTableData.findIndex(
            x => x.id === extract.groupId
          );
          this.groupTableData[groupIndex].status = res.headers.get(
            'X-Progress'
          );
        } else if (res.status === 200) {
          const exportLinks = res.headers.get('Link');
          const links = exportLinks.split(',');
          this.extractData = [];
          for (let p = 0; p < links.length; p++) {
            links[p] = links[p].replace('<', '');
            links[p] = links[p].replace('>', '');
            const dataObj = {
              resourceName: links[p]
                .split('/')
                .pop()
                .split('.')[0],
              resourceLink: links[p]
            };
            this.extractData.push(dataObj);
          }
          const groupIndex = this.groupTableData.findIndex(
            x => x.id === extract.groupId
          );
          this.groupTableData[groupIndex].data = this.extractData;
          this.groupTableData[groupIndex].status = 'Completed';
          this.extractList.pop(extract);
        }
      });
  }
}