import {Injectable} from '@angular/core';
import {catchError, Observable, of, tap} from "rxjs";
import {Club} from "../club/club";
import {HttpClient} from "@angular/common/http";
import {TableEntry} from "../tableEntry/tableEntry";
import {ClubPlace} from "../probability/clubPlace";

@Injectable({
  providedIn: 'root'
})
export class ClubService {

  private apiUrl = 'http://localhost:8080'

  constructor(private http: HttpClient) {
  }

  populateList(): Observable<TableEntry[]> {
    console.log('ClubService: populate list');
    return this.http.get<TableEntry[]>(this.apiUrl + '/populateList').pipe(
      tap(_ => console.log('populated list')), catchError(this.handleError<TableEntry[]>('populateList', [])
      ));
  }

  getClubs(): Observable<Club[]> {
    console.log('ClubService: fetching clubs');
    return this.http.get<Club[]>(this.apiUrl + '/getClubList').pipe(
      tap(_ => console.log('fetched clubs')),
      catchError(this.handleError<Club[]>('getClubs', [])
      ));
  }

  getProbabilities(): Observable<ClubPlace[]> {
    console.log('ClubService: fetching probabilites');
    return this.http.get<ClubPlace[]>(this.apiUrl + '/finishRemainingRandomly').pipe(
      tap(_ => console.log('fetched proabilities')),
      catchError(this.handleError<ClubPlace[]>('getProbabiliies()', [])
      ));
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(error, operation);
      return of(result as T);
    }
  }
}
