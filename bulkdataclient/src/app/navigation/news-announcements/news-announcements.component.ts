import { Component, OnInit, ViewChild } from '@angular/core';
import { Http } from '@angular/http';
import { ModalComponent } from 'ng2-bs3-modal/ng2-bs3-modal';

const newsAndAnnouncementsMarkdownURL = 'https://raw.githubusercontent.com/siteadmin/SITE-Content/master/NewsAndAnnouncements.md';

@Component({
  selector: 'app-news-announcements',
  templateUrl: './news-announcements.component.html',
  styleUrls: ['./news-announcements.component.scss']
})
export class NewsAnnouncementsComponent implements OnInit {
  @ViewChild('newsAndAnnouncementsModal') modal: ModalComponent;
  newsAndAnnouncementsMarkdown: string;

  constructor(private http: Http) {
    this.newsAndAnnouncementsMarkdown = '';
  }

  ngOnInit() {
  }

  getNewsAndAnnouncements() {
    return this.http.get(newsAndAnnouncementsMarkdownURL).map(response => response.text()).subscribe(
      data => {
        this.newsAndAnnouncementsMarkdown = data;
      },
      err => console.error('There was an error: ' + err),
      () => this.modal.open()
    );
  }

}
