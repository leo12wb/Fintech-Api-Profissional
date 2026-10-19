import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterModule } from '@angular/router';
import { AdminService, AdminStats } from '../../../core/services/admin.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, MatProgressSpinnerModule, RouterModule],
  template: `
    <div class="admin-page">
      <div class="page-header">
        <h1>Dashboard</h1>
        <p class="subtitle">Visão geral da plataforma</p>
      </div>

      <div class="stats-grid" *ngIf="stats; else loading">
        <div class="stat-card customers">
          <div class="stat-icon"><mat-icon>group</mat-icon></div>
          <div class="stat-body">
            <span class="stat-value">{{ stats.totalCustomers }}</span>
            <span class="stat-label">Clientes</span>
          </div>
          <a routerLink="/admin/customers" class="stat-link">Ver todos →</a>
        </div>

        <div class="stat-card accounts">
          <div class="stat-icon"><mat-icon>account_balance_wallet</mat-icon></div>
          <div class="stat-body">
            <span class="stat-value">{{ stats.totalAccounts }}</span>
            <span class="stat-label">Contas Abertas</span>
          </div>
        </div>

        <div class="stat-card transactions">
          <div class="stat-icon"><mat-icon>swap_horiz</mat-icon></div>
          <div class="stat-body">
            <span class="stat-value">{{ stats.totalTransactions }}</span>
            <span class="stat-label">Transações</span>
          </div>
        </div>
      </div>

      <ng-template #loading>
        <div class="loading-state">
          <mat-spinner diameter="40"></mat-spinner>
        </div>
      </ng-template>
    </div>
  `,
  styles: [`
    .admin-page { max-width: 1100px; }
    .page-header { margin-bottom: 32px; }
    .page-header h1 { margin: 0; font-size: 1.75rem; font-weight: 700; color: #0f1923; }
    .subtitle { margin: 4px 0 0; color: #6b7280; font-size: 0.9rem; }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
      gap: 20px;
    }
    .stat-card {
      background: #fff;
      border-radius: 12px;
      padding: 24px;
      display: flex;
      flex-direction: column;
      gap: 12px;
      box-shadow: 0 1px 4px rgba(0,0,0,0.06);
      border-left: 4px solid transparent;
      transition: box-shadow 0.2s;
    }
    .stat-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.1); }
    .stat-card.customers { border-left-color: #3f51b5; }
    .stat-card.accounts  { border-left-color: #009688; }
    .stat-card.transactions { border-left-color: #ff9800; }

    .stat-icon { display: flex; }
    .stat-icon mat-icon {
      font-size: 36px; width: 36px; height: 36px;
      color: #6b7280;
    }
    .customers .stat-icon mat-icon { color: #3f51b5; }
    .accounts  .stat-icon mat-icon { color: #009688; }
    .transactions .stat-icon mat-icon { color: #ff9800; }

    .stat-body { display: flex; flex-direction: column; }
    .stat-value { font-size: 2.5rem; font-weight: 700; color: #0f1923; line-height: 1; }
    .stat-label { font-size: 0.875rem; color: #6b7280; margin-top: 4px; }

    .stat-link {
      font-size: 0.8rem; color: #3f51b5;
      text-decoration: none; font-weight: 500;
      margin-top: 4px;
    }
    .stat-link:hover { text-decoration: underline; }

    .loading-state {
      display: flex; justify-content: center; padding: 64px;
    }
  `]
})
export class AdminDashboardComponent implements OnInit {
  stats: AdminStats | null = null;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.adminService.getStats().subscribe(s => this.stats = s);
  }
}
