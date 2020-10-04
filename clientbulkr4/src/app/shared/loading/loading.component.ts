import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
  styleUrls: ['./loading.component.scss']
})
/**
 * A loading spinner that takes in a boolean. This also provides the backdrop on
 * which the spinner will be placed.
 *
 * Example Usage `<app-loading [isLoading]="myBoolean"></app-loading>`. This displays
 * the spinner based on the value of `myBoolean`
 */
export class LoadingComponent {
  @Input() isLoading: boolean;

  constructor() {}
}
