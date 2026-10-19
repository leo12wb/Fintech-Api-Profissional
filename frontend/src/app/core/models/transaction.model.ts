export type TransactionType = 'DEPOSIT' | 'TRANSFER' | 'REVERSAL';
export type TransactionStatus = 'PENDING' | 'COMPLETED' | 'FAILED' | 'REVERSED';
export type Currency = 'BRL' | 'USD';

export interface Transaction {
  id: string; type: TransactionType; sourceAccountId?: string;
  destinationAccountId?: string; amount: number; currency: Currency;
  status: TransactionStatus; idempotencyKey?: string; createdAt: string;
}
export interface DepositRequest { accountId: string; amount: number; currency: Currency; }
export interface TransferRequest {
  sourceAccountId: string; destinationAccountId: string; amount: number; currency: Currency;
}
export interface StatementResponse {
  transactions: Transaction[]; pageNumber: number; pageSize: number;
  totalElements: number; totalPages: number;
}
