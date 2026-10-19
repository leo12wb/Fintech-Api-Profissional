import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Account, BalanceResponse, OpenAccountRequest } from '../models/account.model';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private readonly apiUrl = '/api/v1/accounts';

  constructor(private http: HttpClient) {}

  openAccount(req: OpenAccountRequest): Observable<Account> {
    return this.http.post<Account>(this.apiUrl, req);
  }

  getAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.apiUrl);
  }

  getAccount(id: string): Observable<Account> {
    return this.http.get<Account>(`${this.apiUrl}/${id}`);
  }

  getBalance(id: string): Observable<BalanceResponse> {
    return this.http.get<BalanceResponse>(`${this.apiUrl}/${id}/balance`);
  }
}
