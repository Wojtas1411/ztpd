import { Component, OnInit, Input } from '@angular/core';
import {Document} from '../solr.service';

@Component({
  selector: 'app-document-display',
  templateUrl: './document-display.component.html',
  styleUrls: ['./document-display.component.scss']
})
export class DocumentDisplayComponent implements OnInit {

  @Input()
  public document: Document | undefined;

  constructor() { }

  ngOnInit(): void {
  }

}
