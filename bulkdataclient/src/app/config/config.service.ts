/**
 * Copyright 2018, Xyram Software Solutions. All rights reserved.
 */
import { Injectable } from '@angular/core';
import * as moment from 'moment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import { UtilityService } from '../shared/utility.service';
import { Response, Http } from '@angular/http';
import { Headers } from '@angular/http/src/headers';
import { RequestOptions } from '@angular/http/src/base_request_options';
import { environment } from '../../environments/environment';

@Injectable()
export class ConfigService {
  constructor(private http: HttpClient, private util: UtilityService) { }

  saveSecureConfig(json) {
    return this.http
      .post(environment.apiBaseUrl + '/datasource/secure/', json, {
        observe: 'response'
      })
      .pipe(
        tap(res => {
          return res;
        }),
        catchError(this.handleError('saveConfig', []))
      );
  }

  saveOpenConfig(json) {
    return this.http
      .post(environment.apiBaseUrl + '/datasource/open/', json, {
        observe: 'response'
      })
      .pipe(
        tap(res => {
          return res;
        }),
        catchError(this.handleError('saveConfig', []))
      );
  }

  private handleError(operation, result) {
    return (error: any) => {
      if (operation === 'saveConfig') {
        this.util.notify('Error in saving details.', 'error', error.status);
        return result;
      }
    };
  }
}
