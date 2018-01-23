/**
 * Copyright 2017, Agile SDE, LLC. All rights reserved.
 */
import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UtilityService } from './utility.service';
import { FormsModule } from '@angular/forms';
import { LoadingComponent } from './loading/loading.component';
import { BulkDataMaterialModule } from '../../bulkdata-material.module';

@NgModule({
  imports: [CommonModule, FormsModule, BulkDataMaterialModule],
  declarations: [LoadingComponent],
  exports: [LoadingComponent],
  entryComponents: [],
  providers: [UtilityService]
})
export class SharedModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: SharedModule,
      providers: [UtilityService]
    };
  }
}
