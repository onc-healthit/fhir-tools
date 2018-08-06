/**
 * Copyright 2017, Xyram Software Solutions. All rights reserved.
 */
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './header/header.component';
import { TopNavComponent } from './top-nav/top-nav.component';
import { FooterComponent } from './footer/footer.component';
import { NavBarComponent } from './nav-bar/nav-bar.component';
import { VersionComponent } from './version/version.component';
import { RouterModule } from '@angular/router';
import { BulkDataMaterialModule } from '../../bulkdata-material.module';
import { MarkdownToHtmlPipe } from './MarkdownToHTML.pipe';
import { Ng2Bs3ModalModule } from 'ng2-bs3-modal/ng2-bs3-modal';
import { NewsAnnouncementsComponent } from './news-announcements/news-announcements.component';
import { ReleaseNotesComponent } from './release-notes/release-notes.component';

@NgModule({
  imports: [CommonModule, BulkDataMaterialModule, RouterModule, Ng2Bs3ModalModule],
  declarations: [HeaderComponent, TopNavComponent, FooterComponent, NavBarComponent, VersionComponent, MarkdownToHtmlPipe, NewsAnnouncementsComponent, ReleaseNotesComponent],
  exports: [HeaderComponent, TopNavComponent, FooterComponent, NavBarComponent, VersionComponent, MarkdownToHtmlPipe, NewsAnnouncementsComponent, ReleaseNotesComponent]
})
export class NavigationModule { }
