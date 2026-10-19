import { Component } from '@angular/core';
import { RouterOutlet, RouterModule, RouterLinkActive } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-admin-shell',
  standalone: true,
  imports: [
    RouterOutlet, RouterModule, RouterLinkActive,
    MatSidenavModule, MatListModule, MatIconModule,
    MatButtonModule, MatDividerModule, MatTooltipModule,
  ],
  template: `
    <div class="admin-layout">
      <aside class="admin-sidebar">
        <div class="sidebar-brand">
          <mat-icon class="brand-icon">shield</mat-icon>
          <div>
            <span class="brand-title">NexBank</span>
            <span class="brand-sub">Painel Admin</span>
          </div>
        </div>

        <mat-divider class="sidebar-divider"></mat-divider>

        <nav class="sidebar-nav">
          <a class="nav-item" routerLink="/admin" routerLinkActive="active"
             [routerLinkActiveOptions]="{exact:true}">
            <mat-icon>dashboard</mat-icon>
            <span>Dashboard</span>
          </a>
          <a class="nav-item" routerLink="/admin/customers" routerLinkActive="active">
            <mat-icon>group</mat-icon>
            <span>Clientes</span>
          </a>
        </nav>

        <div class="sidebar-footer">
          <mat-divider class="sidebar-divider"></mat-divider>
          <div class="sidebar-user">
            <mat-icon>manage_accounts</mat-icon>
            <span>Administrador</span>
          </div>
          <button class="nav-item logout-btn" (click)="authService.logout()">
            <mat-icon>logout</mat-icon>
            <span>Sair</span>
          </button>
        </div>
      </aside>

      <main class="admin-content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .admin-layout {
      display: flex;
      min-height: 100vh;
      background: #f0f2f5;
    }
    .admin-sidebar {
      width: 240px;
      min-height: 100vh;
      background: #0f1923;
      display: flex;
      flex-direction: column;
      position: fixed;
      top: 0; left: 0;
      z-index: 100;
    }
    .sidebar-brand {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 24px 20px 20px;
      color: #fff;
    }
    .brand-icon {
      font-size: 32px; width: 32px; height: 32px;
      color: #64b5f6;
    }
    .brand-title {
      display: block;
      font-size: 1rem;
      font-weight: 700;
      letter-spacing: 0.5px;
      color: #fff;
    }
    .brand-sub {
      display: block;
      font-size: 0.7rem;
      color: #64b5f6;
      text-transform: uppercase;
      letter-spacing: 1px;
    }
    .sidebar-divider {
      border-color: rgba(255,255,255,0.08) !important;
      margin: 0 16px;
    }
    .sidebar-nav {
      flex: 1;
      padding: 16px 12px;
      display: flex;
      flex-direction: column;
      gap: 4px;
    }
    .nav-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 10px 12px;
      border-radius: 8px;
      color: rgba(255,255,255,0.65);
      text-decoration: none;
      font-size: 0.875rem;
      font-weight: 500;
      cursor: pointer;
      transition: background 0.15s, color 0.15s;
      background: none;
      border: none;
      width: 100%;
      text-align: left;
    }
    .nav-item:hover {
      background: rgba(255,255,255,0.07);
      color: #fff;
    }
    .nav-item.active {
      background: rgba(100,181,246,0.15);
      color: #64b5f6;
    }
    .nav-item mat-icon { font-size: 20px; width: 20px; height: 20px; }
    .sidebar-footer {
      padding: 0 12px 20px;
    }
    .sidebar-user {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 12px;
      color: rgba(255,255,255,0.4);
      font-size: 0.75rem;
    }
    .sidebar-user mat-icon { font-size: 18px; width: 18px; height: 18px; }
    .logout-btn {
      color: rgba(255,100,100,0.75) !important;
    }
    .logout-btn:hover {
      background: rgba(255,100,100,0.1) !important;
      color: #ff6b6b !important;
    }
    .admin-content {
      margin-left: 240px;
      flex: 1;
      min-height: 100vh;
      padding: 32px;
    }
  `]
})
export class AdminShellComponent {
  constructor(public authService: AuthService) {}
}
