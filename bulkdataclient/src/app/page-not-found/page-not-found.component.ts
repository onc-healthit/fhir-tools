/**
 * Copyright 2018, Xyram Software Solutions. All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';

@Component({
  selector: 'app-page-not-found',
  templateUrl: './page-not-found.component.html',
  styleUrls: ['./page-not-found.component.scss']
})
export class PageNotFoundComponent implements OnInit {
  title = 'Ooops, Page Not Found';
  subTitle = 'Please, return to the previous page';
  constructor(private location: Location) {}

  ngOnInit() {}

  public goBack() {
    this.location.back();
  }
}
