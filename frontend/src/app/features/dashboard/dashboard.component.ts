import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { AccountService } from '../../core/services/account.service';
import { TransactionService } from '../../core/services/transaction.service';
import { Account } from '../../core/models/account.model';
import { Transaction } from '../../core/models/transaction.model';
import { LoadingComponent } from '../../shared/components/loading/loading.component';
import { formatCurrency, formatDate } from '../../core/utils/currency.utils';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, MatCardModule, MatButtonModule, MatIconModule, MatDividerModule, LoadingComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  accounts: Account[] = [];
  recentTransactions: Transaction[] = [];
  loading = true;
  formatCurrency = formatCurrency;
  formatDate = formatDate;

  constructor(private accountService: AccountService, private transactionService: TransactionService) {}

  ngOnInit(): void {
    this.accountService.getAccounts().subscribe({
      next: accounts => {
        this.accounts = accounts;
        if (accounts.length > 0) {
          this.transactionService.getStatement(accounts[0].id, 0, 5).subscribe({
            next: result => { this.recentTransactions = result.transactions; this.loading = false; },
            error: () => this.loading = false
          });
        } else { this.loading = false; }
      },
      error: () => this.loading = false
    });
  }

  get totalBalance(): number {
    return this.accounts.filter(a => a.status === 'ACTIVE').reduce((sum, a) => sum + a.balance, 0);
  }
}
