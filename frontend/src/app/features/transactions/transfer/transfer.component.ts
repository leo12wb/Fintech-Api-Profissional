import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { TransactionService } from '../../../core/services/transaction.service';
import { AccountService } from '../../../core/services/account.service';
import { Account } from '../../../core/models/account.model';
import { ErrorMessageComponent } from '../../../shared/components/error-message/error-message.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { HttpErrorResponse } from '@angular/common/http';
import { formatCurrency } from '../../../core/utils/currency.utils';

@Component({
  selector: 'app-transfer',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, MatCardModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatButtonModule, MatIconModule, MatSnackBarModule, MatDialogModule, ErrorMessageComponent],
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.scss']
})
export class TransferComponent implements OnInit {
  form: FormGroup;
  accounts: Account[] = [];
  loading = false;
  error = '';

  constructor(private fb: FormBuilder, private txService: TransactionService,
    private accountService: AccountService, private router: Router,
    private snackBar: MatSnackBar, private dialog: MatDialog) {
    this.form = this.fb.group({
      sourceAccountId: ['', Validators.required],
      destinationAccountId: ['', [Validators.required]],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      currency: ['BRL', Validators.required]
    });
  }

  ngOnInit(): void {
    this.accountService.getAccounts().subscribe(a => this.accounts = a.filter(acc => acc.status === 'ACTIVE'));
  }

  confirmTransfer(): void {
    if (this.form.invalid) return;
    const { amount, currency, sourceAccountId, destinationAccountId } = this.form.value;
    if (sourceAccountId === destinationAccountId) {
      this.error = 'Conta de origem e destino devem ser diferentes.'; return;
    }
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirmar Transferência',
        message: `Deseja transferir ${formatCurrency(amount, currency)} para a conta selecionada?`,
        confirmLabel: 'Transferir'
      }
    });
    dialogRef.afterClosed().subscribe(confirmed => { if (confirmed) this.submit(); });
  }

  private submit(): void {
    this.loading = true; this.error = '';
    this.txService.transfer(this.form.value).subscribe({
      next: () => {
        this.snackBar.open('Transferência realizada!', 'OK', { duration: 3000 });
        this.router.navigate(['/dashboard']);
      },
      error: (err: HttpErrorResponse) => { this.loading = false; this.error = err.error?.detail ?? 'Erro ao realizar transferência.'; }
    });
  }
}
