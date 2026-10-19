import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: 'accounts',
    canActivate: [authGuard],
    children: [
      { path: '', loadComponent: () => import('./features/accounts/accounts-list/accounts-list.component').then(m => m.AccountsListComponent) },
      { path: 'new', loadComponent: () => import('./features/accounts/open-account/open-account.component').then(m => m.OpenAccountComponent) },
      { path: ':id', loadComponent: () => import('./features/accounts/account-detail/account-detail.component').then(m => m.AccountDetailComponent) },
    ]
  },
  {
    path: 'transactions',
    canActivate: [authGuard],
    children: [
      { path: 'deposit', loadComponent: () => import('./features/transactions/deposit/deposit.component').then(m => m.DepositComponent) },
      { path: 'transfer', loadComponent: () => import('./features/transactions/transfer/transfer.component').then(m => m.TransferComponent) },
      { path: 'statement/:accountId', loadComponent: () => import('./features/transactions/statement/statement.component').then(m => m.StatementComponent) },
    ]
  },
  {
    path: 'admin',
    canActivate: [authGuard, adminGuard],
    loadComponent: () => import('./features/admin/admin-dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent)
  },
  { path: '**', redirectTo: '/dashboard' }
];
