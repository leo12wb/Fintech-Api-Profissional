import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PagedResult } from '../models/api.model';
import { Customer } from '../models/customer.model';

export interface AdminStats {
  totalCustomers: number;
  totalAccounts: number;
  totalTransactions: number;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly apiUrl = '/api/v1/admin';

  constructor(private http: HttpClient) {}

  getStats(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.apiUrl}/stats`);
  }

  listCustomers(page = 0, size = 20): Observable<PagedResult<Customer>> {
    return this.http.get<PagedResult<Customer>>(
      `${this.apiUrl}/customers?page=${page}&size=${size}`
    );
  }
}
