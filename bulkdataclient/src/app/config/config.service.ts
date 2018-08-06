import { Subject } from 'rxjs/Subject';
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

  isLoadingResults: boolean;
  // EventEmitter should not be used this way - only for `@Output()`s
  // nameChange: EventEmitter<string> = new EventEmitter<string>();
  loading: Subject<boolean> = new Subject<boolean>();

  constructor(private http: HttpClient, private util: UtilityService) {
    this.isLoadingResults = false;
  }

  change() {
    this.isLoadingResults = false;
    this.loading.next(this.isLoadingResults);
  }

  exportAll() {
    this.isLoadingResults = true;
    this.loading.next(this.isLoadingResults);
    const headers = new HttpHeaders({
      Accept: 'text/plain',
      Prefer: 'respond-async',
      'dsId': this.util.dsId.toString()
    });
    return this.http.get(environment.apiBaseUrl + '/fhir/Patient/$export', {
      headers: headers,
      observe: 'response'
    })
      .pipe(
        tap(res => {
          return res;
        }),
        catchError(this.handleError('exportAll', []))
      );
  }


  getGroupById(id: any) {
    this.isLoadingResults = true;
    this.loading.next(this.isLoadingResults);
    const headers = new HttpHeaders({
      Accept: 'application/fhir+ndjson',
      Prefer: 'respond-async',
      'dsId': this.util.dsId.toString()
    });
    return this.http
      .get(environment.apiBaseUrl + '/fhir/Group/' + id + '/$export', {
        headers: headers,
        observe: 'response'
      })
      .pipe(
        tap(res => {
          return res;
        }),
        catchError(this.handleError('getGroupById', []))
      );
  }

  getByDateandResources(date, resources) {
    this.isLoadingResults = true;
    this.loading.next(this.isLoadingResults);
    const headers = new HttpHeaders({
      Accept: 'text/plain',
      Prefer: 'respond-async',
      'dsId': this.util.dsId.toString()
    });
    return this.http
      .get(environment.apiBaseUrl + '/fhir/Patient/$export?_since=' + date + '&_type=' + resources, {
        headers: headers,
        observe: 'response'
      })
      .pipe(
        tap(res => {
          return res;
        }),
        catchError(this.handleError('getByDateandResources', []))
      );
  }

  getByGroupDateandResources(groupId, date, resources) {
    this.isLoadingResults = true;
    this.loading.next(this.isLoadingResults);
    const headers = new HttpHeaders({
      Accept: 'text/plain',
      Prefer: 'respond-async',
      'dsId': this.util.dsId.toString()
    });
    return this.http
      .get(environment.apiBaseUrl + '/fhir/Group/' + groupId + '/$export?_since=' + date + '&_type=' + resources, {
        headers: headers,
        observe: 'response'
      })
      .pipe(
        tap(res => {
          return res;
        }),
        catchError(this.handleError('getByGroupDateandResources', []))
      );
  }

  getBulkDataByContentLocation(location: any) {
    return this.http
      .get(location, {
        headers: new HttpHeaders().set('dsId', this.util.dsId.toString()),
        observe: 'response'
      })
      .pipe(
        tap(res => {
          console.log(res);
          if (res.status === 200) {
            this.isLoadingResults = false;
            this.loading.next(this.isLoadingResults);
          }
          return res;
        }),
        catchError(this.handleError('getBulkDataByContentLocation', []))
      );
  }


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

  getSelectedNdJson(resourceLink) {
    return this.http
      .get(resourceLink, {
        // headers: new HttpHeaders().set('mode', this.util.fhirmode),
        observe: 'response',
        responseType: 'text'
      })
      .pipe(
        tap(res => {
          return res;
        }),
        catchError(this.handleError('getSelectedNdJson', []))
      );
  }

  validateURL(url) {
    return this.http
      .get(environment.apiBaseUrl + '/validate/?serverBase=' + url)
      .pipe(
        tap(res => {
          return res;
        }),
        catchError(this.handleError('validateURL', []))
      );
  }

  private handleError(operation, result) {
    return (error: any) => {
      console.log(error.error.status);
      if (operation === 'saveConfig') {
        this.isLoadingResults = false;
        this.loading.next(this.isLoadingResults);
        this.util.notify('Error in saving details.', 'error', error.status);
        return result;
      } else if (operation === 'exportAll' || operation === 'getByGroupDateandResources' || operation === 'getGroupById' || operation === 'getByDateandResources' || operation === 'getBulkDataByContentLocation' || operation === 'getByGroupDateandResources') {
        this.isLoadingResults = false;
        this.loading.next(this.isLoadingResults);
        this.util.notify(
          'Error in Extracting the Bulk Data',
          'error',
          error.status
        );
        return result;
      } else if (operation === 'validateURL') {
        this.isLoadingResults = false;
        this.loading.next(this.isLoadingResults);
        this.util.notify(
          'The Provided URL does not support Patient/$export',
          'error',
          error.error.status
        );
      }
    };
  }
}
