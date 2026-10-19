import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest, DecodedToken } from '../models/auth.model';
import { Customer } from '../models/customer.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = '/api/v1/auth';
  private readonly TOKEN_KEY = 'nexbank_access_token';
  private readonly REFRESH_KEY = 'nexbank_refresh_token';

  currentUser = signal<Customer | null>(null);

  constructor(private http: HttpClient, private router: Router) {}

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, req).pipe(
      tap(resp => this.storeTokens(resp))
    );
  }

  register(req: RegisterRequest): Observable<Customer> {
    return this.http.post<Customer>(`${this.apiUrl}/register`, req);
  }

  logout(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
    sessionStorage.removeItem(this.REFRESH_KEY);
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  getAccessToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    const token = this.getAccessToken();
    if (!token) return false;
    try {
      const decoded = this.decodeToken(token);
      return decoded.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  isAdmin(): boolean {
    const token = this.getAccessToken();
    if (!token) return false;
    try {
      const decoded = this.decodeToken(token);
      return decoded.roles?.includes('ADMIN') ?? false;
    } catch {
      return false;
    }
  }

  getCustomerId(): string | null {
    const token = this.getAccessToken();
    if (!token) return null;
    try {
      return this.decodeToken(token).sub;
    } catch {
      return null;
    }
  }

  private storeTokens(resp: AuthResponse): void {
    // Store in sessionStorage (cleared on tab close — reasonable for demo)
    sessionStorage.setItem(this.TOKEN_KEY, resp.accessToken);
    sessionStorage.setItem(this.REFRESH_KEY, resp.refreshToken);
  }

  private decodeToken(token: string): DecodedToken {
    const payload = token.split('.')[1];
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decoded) as DecodedToken;
  }
}
