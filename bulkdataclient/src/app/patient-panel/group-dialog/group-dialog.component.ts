/**
 * Copyright 2018, Xyram Software Solutions. All rights reserved.
 */
import { Component, OnInit, Inject, ViewEncapsulation } from '@angular/core';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-group-dialog',
  templateUrl: './group-dialog.component.html',
  styleUrls: ['./group-dialog.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class GroupDialogComponent implements OnInit {
  groupName: any;
  groupNameForm: FormGroup;
  constructor(
    public dialogRef: MatDialogRef<GroupDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit() {
    this.groupNameForm = new FormGroup({
      groupName: new FormControl('', [Validators.required])
    });
  }

  onClose(): void {
    this.dialogRef.close(this.groupNameForm.get('groupName').value);
  }
}
