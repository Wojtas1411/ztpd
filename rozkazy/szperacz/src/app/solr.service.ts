import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {Observable} from 'rxjs';

export class Document {
  public id = '';
  // tslint:disable-next-line:variable-name
  public last_modified = '';
  // tslint:disable-next-line:variable-name
  public content_type = '';
  public resourcename: string[] = [];
  public author = '';
  // tslint:disable-next-line:variable-name
  public author_s = '';
  public content: string[] = [];
}

export class SolrResponse{
  public responseHeader: {status: number, QTime: number, params: any };
  public response: { numFound: number, start: number, numFoundExact: boolean, docs: Document[] };

  constructor(
    responseHeader: { status: number; QTime: number; params: any },
    response: { numFound: number; start: number; numFoundExact: boolean; docs: Document[] }
    ) {
    this.responseHeader = responseHeader;
    this.response = response;
  }
}

@Injectable({
  providedIn: 'root'
})
export class SolrService {

  private url = environment.solrUrl + '/solr/rozkazy/';

  constructor(private http: HttpClient) { }

  public query(query: string): Observable<SolrResponse> {
    return this.http.get<SolrResponse>(`${this.url}select?q=${query}`);
  }
}
