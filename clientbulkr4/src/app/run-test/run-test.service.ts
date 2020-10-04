import { Injectable } from '@angular/core';
import * as moment from 'moment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import { UtilityService } from '../shared/utility.service';
import { Response, Http } from '@angular/http';
import { RequestOptions } from '@angular/http/src/base_request_options';
import { Headers } from '@angular/http/src/headers';
import { environment } from '../../environments/environment';

@Injectable()
export class RunTestService {
  constructor(private http: HttpClient, private util: UtilityService) { }

  getGroupById(id, location) {
    const headers = new HttpHeaders({
      Accept: 'application/fhir+ndjson',
      Prefer: 'respond-async',
      dsId: '19'
    });
    return this.http
      .get(location + '/fhir/Group/' + id + '/$export', {
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

  exportUsingParams(location) {
    const headers = new HttpHeaders({
      // Accept: 'application/fhir+ndjson',
      // Prefer: 'respond-async',
      dsId: '19'
    });
    return this.http
      .get(location, {
        headers: headers,
        observe: 'response'
      })
      .pipe(
        tap(res => {
          return res;
        }),
        catchError(this.handleError('exportUsingParams', []))
      );
  }

  getBulkDataByContentLocation(location: any) {
    return this.http
      .get(location, {
        headers: new HttpHeaders().set('dsId', '36'),
        observe: 'response'
      })
      .pipe(
        tap(res => {
          return res;
        }),
        catchError(this.handleError('getBulkDataByContentLocation', []))
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

  private handleError(operation, result) {
    return (error: any) => {
      if (operation === 'getGroupById') {
        this.util.notify(
          'Error in fetching Group By Id',
          'error',
          error.status
        );
        return result;
      } else if (operation === 'getBulkDataByContentLocation') {
        this.util.notify(
          'Error in fetching Bulk Data',
          'error',
          error.status
        );
        return result;
      } else if (operation === 'getSelectedNdJson') {
        this.util.notify(
          'Error in fetching Selected NdJson File.',
          'error',
          error.status
        );
        return result;
      } else if (operation === 'exportUsingParams') {
        this.util.notify(
          'Error in extracting the data.',
          'error',
          error.status
        );
        return result;
      }
    };
  }
}
