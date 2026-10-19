import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatChipsModule } from '@angular/material/chips';
import { TransactionService } from '../../../core/services/transaction.service';
import { Transaction } from '../../../core/models/transaction.model';
import { LoadingComponent } from '../../../shared/components/loading/loading.component';
import { formatCurrency, formatDate } from '../../../core/utils/currency.utils';

@Component({
  selector: 'app-statement',
  standalone: true,
  imports: [CommonModule, RouterModule, MatCardModule, MatTableModule, MatButtonModule, MatIconModule, MatPaginatorModule, MatChipsModule, LoadingComponent],
  templateUrl: './statement.component.html',
  styleUrls: ['./statement.component.scss']
})
export class StatementComponent implements OnInit {
  @Input() accountId = '';
  transactions: Transaction[] = [];
  loading = true;
  totalElements = 0;
  pageSize = 20;
  displayedColumns = ['type', 'amount', 'direction', 'status', 'date'];
  formatCurrency = formatCurrency;
  formatDate = formatDate;

  constructor(private txService: TransactionService) {}

  ngOnInit(): void { this.loadPage(0); }

  loadPage(page: number): void {
    this.loading = true;
    this.txService.getStatement(this.accountId, page, this.pageSize).subscribe({
      next: r => { this.transactions = r.transactions; this.totalElements = r.totalElements; this.loading = false; },
      error: () => this.loading = false
    });
  }

  onPageChange(event: PageEvent): void { this.loadPage(event.pageIndex); }

  getStatusClass(status: string): string {
    return `chip-${status.toLowerCase()}`;
  }
}
