import { Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-error-message',
  standalone: true,
  imports: [MatIconModule, CommonModule],
  template: `
    @if (message) {
      <div class="error-banner">
        <mat-icon>error_outline</mat-icon>
        <span>{{ message }}</span>
      </div>
    }
  `,
  styles: [`.error-banner { display:flex; align-items:center; gap:8px; background:#ffebee; color:#c62828; padding:12px 16px; border-radius:8px; margin-bottom:16px; } mat-icon { font-size:20px; width:20px; height:20px; }`]
})
export class ErrorMessageComponent {
  @Input() message = '';
}
