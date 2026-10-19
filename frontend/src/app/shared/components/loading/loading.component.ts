import { Component, Input } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loading',
  standalone: true,
  imports: [MatProgressSpinnerModule, CommonModule],
  template: `
    @if (loading) {
      <div class="loading-container">
        <mat-spinner diameter="40"></mat-spinner>
        @if (message) { <p class="loading-message">{{ message }}</p> }
      </div>
    }
  `,
  styles: [`.loading-container { display:flex; flex-direction:column; align-items:center; padding:32px; gap:16px; } .loading-message { color: #757575; }`]
})
export class LoadingComponent {
  @Input() loading = false;
  @Input() message = '';
}
