import { Component } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { NavComponent } from './shared/components/nav/nav.component';
import { CommonModule } from '@angular/common';
import { AuthService } from './core/services/auth.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavComponent, CommonModule],
  template: `
    <app-nav *ngIf="authService.isLoggedIn() && !isAdminRoute"></app-nav>
    <main [class.with-nav]="authService.isLoggedIn() && !isAdminRoute">
      <router-outlet></router-outlet>
    </main>
  `,
  styles: [`
    main { min-height: 100vh; }
    main.with-nav { padding-top: 64px; }
  `]
})
export class AppComponent {
  isAdminRoute = false;

  constructor(public authService: AuthService, private router: Router) {
    this.router.events.pipe(
      filter((e): e is NavigationEnd => e instanceof NavigationEnd)
    ).subscribe(e => {
      this.isAdminRoute = e.urlAfterRedirects.startsWith('/admin');
    });
  }
}
