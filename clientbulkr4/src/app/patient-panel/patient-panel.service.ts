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
import { environment } from '../../environments/environment';

@Injectable()
export class PatientPanelService {
  constructor(private http: HttpClient, private util: UtilityService) { }

  getListofAllPatients() {
    console.log('In Call' + this.util.dsId);
    return this.http
      .get(environment.apiBaseUrl + '/fhir/Patient?_format=json', {
        headers: new HttpHeaders().set('dsId', this.util.dsId.toString()),
        observe: 'response'
      })
      .pipe(
        tap(res => {
          return res;
        }),
        catchError(this.handleError('getListofAllPatients', []))
      );
  }

  createGroups(selectedPatients: any, groupName: any) {
    const params = new URLSearchParams();
    const json = {
      resourceType: 'Group',
      type: 'person',
      name: groupName,
      actual: true,
      member: selectedPatients
    };
    const headers = new Headers({ 'Content-Type': 'application/fhir+json' });
    return this.http
      .post(environment.apiBaseUrl + '/fhir/Group/', json, {
        headers: new HttpHeaders().set('dsId', this.util.dsId.toString()),
        observe: 'response'
      })
      .pipe(
        tap(res => {
          if (res.status === 201) {
            return res;
          } else {
            this.util.notify(
              'Error in creating groups',
              'error',
              `{res.status}`
            );
          }
        }),
        catchError(this.handleError('createGroupError', []))
      );
  }

  private handleError(operation, result) {
    return (error: any) => {
      if (operation === 'getListofAllPatients') {
        this.util.notify(
          'Error in fetching list of patients.',
          'error',
          error.status
        );
        return result;
      } else if (operation === 'createGroupError') {
        this.util.notify('Error in creating groups', 'error', error.status);
        return result;
      }
    };
  }
}
