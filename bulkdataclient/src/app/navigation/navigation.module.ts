/**
 * Copyright 2017, Xyram Software Solutions. All rights reserved.
 */
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './header/header.component';
import { NavBarComponent } from './nav-bar/nav-bar.component';
import { RouterModule } from '@angular/router';
import { BulkDataMaterialModule } from '../../bulkdata-material.module';

@NgModule({
  imports: [CommonModule, BulkDataMaterialModule, RouterModule],
  declarations: [HeaderComponent, NavBarComponent],
  exports: [HeaderComponent, NavBarComponent]
})
export class NavigationModule {}
