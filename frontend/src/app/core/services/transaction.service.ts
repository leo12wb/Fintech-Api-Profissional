import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Transaction, DepositRequest, TransferRequest, StatementResponse } from '../models/transaction.model';

// Generate a simple UUID without external dependency
function generateUUID(): string {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    const r = Math.random() * 16 | 0;
    return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
  });
}

@Injectable({ providedIn: 'root' })
export class TransactionService {
  private readonly apiUrl = '/api/v1/transactions';

  constructor(private http: HttpClient) {}

  deposit(req: DepositRequest): Observable<Transaction> {
    const headers = new HttpHeaders({ 'Idempotency-Key': generateUUID() });
    return this.http.post<Transaction>(`${this.apiUrl}/deposits`, req, { headers });
  }

  transfer(req: TransferRequest): Observable<Transaction> {
    const headers = new HttpHeaders({ 'Idempotency-Key': generateUUID() });
    return this.http.post<Transaction>(`${this.apiUrl}/transfers`, req, { headers });
  }

  getStatement(accountId: string, page = 0, size = 20): Observable<StatementResponse> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<StatementResponse>(`/api/v1/accounts/${accountId}/statement`, { params });
  }
}
