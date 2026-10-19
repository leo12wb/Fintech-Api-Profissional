export type CustomerRole = 'USER' | 'ADMIN';
export type CustomerStatus = 'ACTIVE' | 'INACTIVE' | 'BLOCKED';
export interface Customer {
  id: string; name: string; email: string;
  role: CustomerRole; status: CustomerStatus; createdAt: string;
}
