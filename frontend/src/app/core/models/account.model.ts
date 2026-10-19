export type AccountStatus = 'ACTIVE' | 'BLOCKED' | 'CLOSED';
export type Currency = 'BRL' | 'USD';

export interface Account {
  id: string; accountNumber: string; currency: Currency;
  balance: number; status: AccountStatus; openedAt: string;
}
export interface BalanceResponse {
  accountNumber: string; balance: number; currency: Currency; status: AccountStatus;
}
export interface OpenAccountRequest { currency: Currency; }
