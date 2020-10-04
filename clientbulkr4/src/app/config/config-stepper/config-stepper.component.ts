import { NdJsonDialogComponent } from './../../extract/ndjson-dialog/ndjson-dialog.component';

import { Component, OnInit, Input } from '@angular/core';
import { ConfigService } from './../config.service';
import { MatDialog } from '@angular/material';

@Component({
  selector: 'app-config-stepper',
  templateUrl: './config-stepper.component.html',
  styleUrls: ['./config-stepper.component.scss']
})
export class ConfigStepperComponent implements OnInit {

  @Input() data;
  getApiData: any = {};
  getPollData: any = {};
  getDownloadData: any = {};
  downloadContent: any = [];
  constructor(private configService: ConfigService, private dialog: MatDialog) {

  }
  ngOnInit() {
    console.log(this.data);
    this.getApiData = this.data.getApiData;
    this.getPollData = this.data.getPollData;
    this.getDownloadData = this.data.getDownloadData;
    this.downloadContent = this.getDownloadData;
  }

  getSelectedNdJson(resourceName, resourceLink) {
    this.configService
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
