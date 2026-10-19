import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AdminService } from '../../../core/services/admin.service';
import { Customer } from '../../../core/models/customer.model';
import { PagedResult } from '../../../core/models/api.model';

@Component({
  selector: 'app-admin-customers',
  standalone: true,
  imports: [
    CommonModule, MatTableModule, MatPaginatorModule,
    MatIconModule, MatChipsModule, MatProgressSpinnerModule,
  ],
  template: `
    <div class="admin-page">
      <div class="page-header">
        <h1>Clientes</h1>
        <p class="subtitle">{{ result?.totalElements ?? 0 }} clientes cadastrados</p>
      </div>

      <div class="table-card">
        <div *ngIf="loading" class="loading-overlay">
          <mat-spinner diameter="36"></mat-spinner>
        </div>

        <table mat-table [dataSource]="customers" *ngIf="!loading">
          <ng-container matColumnDef="name">
            <th mat-header-cell *matHeaderCellDef>Nome</th>
            <td mat-cell *matCellDef="let c">
              <div class="customer-name">
                <span class="avatar">{{ c.name.charAt(0).toUpperCase() }}</span>
                {{ c.name }}
              </div>
            </td>
          </ng-container>

          <ng-container matColumnDef="email">
            <th mat-header-cell *matHeaderCellDef>Email</th>
            <td mat-cell *matCellDef="let c">{{ c.email }}</td>
          </ng-container>

          <ng-container matColumnDef="role">
            <th mat-header-cell *matHeaderCellDef>Role</th>
            <td mat-cell *matCellDef="let c">
              <mat-chip [class]="'chip-role-' + c.role.toLowerCase()">
                <mat-icon matChipAvatar>{{ c.role === 'ADMIN' ? 'shield' : 'person' }}</mat-icon>
                {{ c.role }}
              </mat-chip>
            </td>
          </ng-container>

          <ng-container matColumnDef="status">
            <th mat-header-cell *matHeaderCellDef>Status</th>
            <td mat-cell *matCellDef="let c">
              <span [class]="'status-badge status-' + c.status.toLowerCase()">
                {{ c.status }}
              </span>
            </td>
          </ng-container>

          <ng-container matColumnDef="createdAt">
            <th mat-header-cell *matHeaderCellDef>Cadastro</th>
            <td mat-cell *matCellDef="let c">{{ c.createdAt | date:'dd/MM/yyyy HH:mm' }}</td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="columns"></tr>
          <tr mat-row *matRowDef="let row; columns: columns"></tr>
        </table>

        <mat-paginator
          *ngIf="result"
          [length]="result.totalElements"
          [pageSize]="pageSize"
          [pageSizeOptions]="[10, 20, 50]"
          (page)="onPage($event)">
        </mat-paginator>
      </div>
    </div>
  `,
  styles: [`
    .admin-page { max-width: 1100px; }
    .page-header { margin-bottom: 24px; }
    .page-header h1 { margin: 0; font-size: 1.75rem; font-weight: 700; color: #0f1923; }
    .subtitle { margin: 4px 0 0; color: #6b7280; font-size: 0.9rem; }

    .table-card {
      background: #fff;
      border-radius: 12px;
      box-shadow: 0 1px 4px rgba(0,0,0,0.06);
      overflow: hidden;
      position: relative;
    }
    .loading-overlay {
      display: flex; justify-content: center;
      padding: 64px;
    }

    table { width: 100%; }
    th.mat-header-cell {
      font-size: 0.75rem; font-weight: 600;
      text-transform: uppercase; letter-spacing: 0.5px;
      color: #6b7280;
      background: #f9fafb;
      border-bottom: 1px solid #e5e7eb;
    }
    td.mat-cell {
      font-size: 0.875rem;
      color: #374151;
      border-bottom: 1px solid #f3f4f6;
    }
    tr.mat-row:hover td { background: #f9fafb; }

    .customer-name {
      display: flex; align-items: center; gap: 10px; font-weight: 500;
    }
    .avatar {
      width: 32px; height: 32px;
      border-radius: 50%;
      background: #e8eaf6;
      color: #3f51b5;
      display: flex; align-items: center; justify-content: center;
      font-size: 0.8rem; font-weight: 700;
      flex-shrink: 0;
    }

    .chip-role-admin {
      background: #e8eaf6 !important; color: #3f51b5 !important;
      font-size: 0.75rem !important;
    }
    .chip-role-user {
      background: #f3f4f6 !important; color: #6b7280 !important;
      font-size: 0.75rem !important;
    }

    .status-badge {
      display: inline-block;
      padding: 2px 10px;
      border-radius: 20px;
      font-size: 0.75rem; font-weight: 600;
      text-transform: uppercase; letter-spacing: 0.5px;
    }
    .status-active   { background: #dcfce7; color: #166534; }
    .status-inactive { background: #f3f4f6; color: #6b7280; }
    .status-blocked  { background: #fee2e2; color: #991b1b; }
  `]
})
export class AdminCustomersComponent implements OnInit {
  columns = ['name', 'email', 'role', 'status', 'createdAt'];
  customers: Customer[] = [];
  result: PagedResult<Customer> | null = null;
  loading = true;
  page = 0;
  pageSize = 20;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void { this.load(); }

  onPage(e: PageEvent): void {
    this.page = e.pageIndex;
    this.pageSize = e.pageSize;
    this.load();
  }

  private load(): void {
    this.loading = true;
    this.adminService.listCustomers(this.page, this.pageSize).subscribe(r => {
      this.result = r;
      this.customers = r.content;
      this.loading = false;
    });
  }
}
