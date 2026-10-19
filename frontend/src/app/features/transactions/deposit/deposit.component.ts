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
import { TransactionService } from '../../../core/services/transaction.service';
import { AccountService } from '../../../core/services/account.service';
import { Account } from '../../../core/models/account.model';
import { ErrorMessageComponent } from '../../../shared/components/error-message/error-message.component';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-deposit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, MatCardModule, MatFormFieldModule,
    MatInputModule, MatSelectModule, MatButtonModule, MatIconModule, MatSnackBarModule, ErrorMessageComponent],
  templateUrl: './deposit.component.html',
  styleUrls: ['./deposit.component.scss']
})
export class DepositComponent implements OnInit {
  form: FormGroup;
  accounts: Account[] = [];
  loading = false;
  error = '';

  constructor(private fb: FormBuilder, private txService: TransactionService,
    private accountService: AccountService, private router: Router, private snackBar: MatSnackBar) {
    this.form = this.fb.group({
      accountId: ['', Validators.required],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      currency: ['BRL', Validators.required]
    });
  }

  ngOnInit(): void {
    this.accountService.getAccounts().subscribe(a => this.accounts = a.filter(acc => acc.status === 'ACTIVE'));
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true; this.error = '';
    this.txService.deposit(this.form.value).subscribe({
      next: () => {
        this.snackBar.open('Depósito realizado com sucesso!', 'OK', { duration: 3000 });
        this.router.navigate(['/dashboard']);
      },
      error: (err: HttpErrorResponse) => { this.loading = false; this.error = err.error?.detail ?? 'Erro ao realizar depósito.'; }
    });
  }
}
