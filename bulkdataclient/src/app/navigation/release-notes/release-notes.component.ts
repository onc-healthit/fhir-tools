import {Component, OnInit, ViewChild} from "@angular/core";
import {Http} from "@angular/http";
import {ModalComponent} from "ng2-bs3-modal/ng2-bs3-modal";

const releaseNotesMarkdownURL = 'https://raw.githubusercontent.com/siteadmin/SITE-Content/master/ReleaseNotes.md';
@Component({
  selector: 'app-release-notes',
  templateUrl: './release-notes.component.html',
  styleUrls: ['./release-notes.component.scss']
})
export class ReleaseNotesComponent implements OnInit {
  @ViewChild('releaseNotesModal') modal: ModalComponent;
  releaseNotesMarkdown: string;

  constructor(private http:Http) {
    this.releaseNotesMarkdown = '';
  }

  ngOnInit() {
  }

  public getReleaseNotes(){
    return this.http.get(releaseNotesMarkdownURL).map(response => response.text()).subscribe(
      data => {
        this.releaseNotesMarkdown = data
      },
      err => console.error('There was an error: ' + err),
      () => this.modal.open()
    );
  }
}
