import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule],
  template: `
    <div class="nexbank-container">
      <h1 class="page-title">Administração</h1>
      <mat-card class="nexbank-card">
        <mat-card-content>
          <div class="admin-info">
            <mat-icon>admin_panel_settings</mat-icon>
            <div>
              <h2>Área Restrita — ADMIN</h2>
              <p>Acesso restrito a administradores do sistema.</p>
              <p class="text-muted">Use a API diretamente em /api/v1/admin/* para operações administrativas.</p>
            </div>
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`.admin-info { display: flex; gap: 24px; align-items: flex-start; } mat-icon { font-size: 48px; width: 48px; height: 48px; color: #3f51b5; } h2 { margin: 0 0 8px; }`]
})
export class AdminDashboardComponent {}
