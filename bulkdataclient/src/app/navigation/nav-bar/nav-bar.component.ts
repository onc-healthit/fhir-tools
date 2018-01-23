/**
 * Copyright 2018, Xyram Software Solutions. All rights reserved.
 */
import {
  Component,
  OnInit,
  HostListener,
  ViewEncapsulation
} from '@angular/core';
import { AppRoutingModule } from '../../app-routing.module';

@Component({
  selector: 'app-navbar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss']
})
export class NavBarComponent implements OnInit {
  constructor(private siteRouting: AppRoutingModule) {}

  ngOnInit() {}
}
