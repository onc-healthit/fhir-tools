/**
 * Copyright 2018, Xyram Software Solutions. All rights reserved.
 */
import { Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  origin: string;
  constructor() { }

  ngOnInit() {
    this.origin = location.origin;
  }
}
