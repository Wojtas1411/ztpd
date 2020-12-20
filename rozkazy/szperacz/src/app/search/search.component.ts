import { Component, OnInit } from '@angular/core';
import {SolrResponse, SolrService} from '../solr.service';
import {map, tap} from 'rxjs/operators';

function parseDocumentContent(content: string): string {
  return content.trim()
    .replace(/[ ]{2,}/g, ' ')
    .replace(/\n /g, '\n')
    .replace(/[\n]{2,3}/g, '\n');
}

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {

  public response: SolrResponse | undefined;

  public query = '';

  constructor(private solrService: SolrService) {
    this.response = undefined;
  }

  private doSearch(q: string): void {
    this.solrService.query(q).pipe(
      map(response => {
        response.response.docs = response.response.docs.map(doc => {
          doc.content = doc.content.map(parseDocumentContent);
          return doc;
        });
        return response;
      }),
      tap(response => response.response.docs.sort((a, b) => Date.parse(a.last_modified) - Date.parse(b.last_modified)))
    ).subscribe(
      data => this.response = data,
      error => console.error(error)
    );
  }

  ngOnInit(): void {
    this.doSearch('Taisner');
  }

  public onSearch(): void {
    this.doSearch(this.query);
  }

}
