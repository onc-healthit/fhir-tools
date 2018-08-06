/**
 * Copyright 2018, Xyram Software Solutions. All rights reserved.
 */
import {
  Component,
  OnInit,
  Inject,
  ViewEncapsulation,
  ViewChild
} from '@angular/core';
import {
  MatDialog,
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatTableDataSource,
  MatPaginator
} from '@angular/material';
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-ndjson-dialog',
  templateUrl: './ndjson-dialog.component.html',
  styleUrls: ['./ndjson-dialog.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class NdJsonDialogComponent implements OnInit {
  ndjsondata: any;
  ndjsonname: any;
  ndjsonlink: any;
  constructor(
    public dialogRef: MatDialogRef<NdJsonDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) { }

  ngOnInit() {
    const jsonList = [];
    console.log(this.data.ndjson);
    if (this.data.ndjson !== '') {
      const jsonobj = this.data.ndjson.split('\n');
      for (let i = 0; i < jsonobj.length; i++) {
        jsonList.push(JSON.parse(jsonobj[i]));
      }
      this.ndjsondata = jsonList;
    } else {
      this.ndjsondata = this.data.ndjson;
    }

    this.ndjsonname = this.data.name;
    this.ndjsonlink = this.data.link;
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
