import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { AccountService } from '../../../core/services/account.service';
import { Account } from '../../../core/models/account.model';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';
import { formatCurrency, formatDate } from '../../../core/utils/currency.utils';

@Component({
  selector: 'app-account-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, MatCardModule, MatButtonModule, MatIconModule, MatDividerModule, LoadingComponent],
  templateUrl: './account-detail.component.html',
  styleUrls: ['./account-detail.component.scss']
})
export class AccountDetailComponent implements OnInit {
  @Input() id = '';
  account: Account | null = null;
  loading = true;
  formatCurrency = formatCurrency;
  formatDate = formatDate;

  constructor(private accountService: AccountService) {}

  ngOnInit(): void {
    this.accountService.getAccount(this.id).subscribe({
      next: a => { this.account = a; this.loading = false; },
      error: () => this.loading = false
    });
  }
}
