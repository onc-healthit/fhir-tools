import { Component, ViewEncapsulation } from '@angular/core';
import { MatSidenav } from '@angular/material';
import { SimpleNotificationsComponent } from 'angular2-notifications';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class AppComponent {
  notificationOptions = {
    timeOut: 5000,
    showProgressBar: true,
    pauseOnHover: true,
    position: ['bottom', 'right']
  };
  constructor() {}
}
