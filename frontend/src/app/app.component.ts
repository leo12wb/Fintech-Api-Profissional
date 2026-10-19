import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavComponent } from './shared/components/nav/nav.component';
import { CommonModule } from '@angular/common';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavComponent, CommonModule],
  template: `
    <app-nav *ngIf="authService.isLoggedIn()"></app-nav>
    <main [class.with-nav]="authService.isLoggedIn()">
      <router-outlet></router-outlet>
    </main>
  `,
  styles: [`
    main { min-height: 100vh; }
    main.with-nav { padding-top: 64px; }
  `]
})
export class AppComponent {
  constructor(public authService: AuthService) {}
}
