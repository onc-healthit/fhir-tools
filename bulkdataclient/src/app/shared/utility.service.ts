/**
 * Copyright 2018, Xyram Software Solutions. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { NotificationsService } from 'angular2-notifications/dist';
import * as _ from 'lodash';

export type NotificationType = 'success' | 'error' | 'alert' | 'warn' | 'info';

@Injectable()
export class UtilityService {
  fhirServerURL: any;
  fhirmode: any;
  dsId: any;
  constructor(private notificationService: NotificationsService) { }

  public static stripREST(data) {
    if (_.isFunction(data.plain)) {
      return data.plain();
    } else {
      return data;
    }
  }

  public notify(
    message: string,
    type: NotificationType = 'info',
    title?: string,
    overrideOptions?: any
  ) {
    this.notificationService[type](
      title || this.capitalizeFirstChar(type),
      message,
      overrideOptions
    );
  }

  public capitalizeFirstChar(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
  }

  convertEnumToString(string) {
    return string.replace('_', ' ');
  }

  /**
   * Returns true if the string exists and is greater than minimum(in this case 2)
   * @param change the input value from search box
   */
  isMinimumInputLength(change: string): boolean {
    return change ? change.length > 2 : false;
  }
}
