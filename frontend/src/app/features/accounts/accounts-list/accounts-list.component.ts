import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { AccountService } from '../../../core/services/account.service';
import { Account } from '../../../core/models/account.model';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';
import { formatCurrency } from '../../../core/utils/currency.utils';

@Component({
  selector: 'app-accounts-list',
  standalone: true,
  imports: [CommonModule, RouterModule, MatCardModule, MatButtonModule, MatIconModule, MatTableModule, LoadingComponent],
  templateUrl: './accounts-list.component.html',
  styleUrls: ['./accounts-list.component.scss']
})
export class AccountsListComponent implements OnInit {
  accounts: Account[] = [];
  loading = true;
  displayedColumns = ['accountNumber', 'currency', 'balance', 'status', 'actions'];
  formatCurrency = formatCurrency;

  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    this.accountService.getAccounts().subscribe({
      next: a => { this.accounts = a; this.loading = false; },
      error: () => this.loading = false
    });
  }
}
