import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AccountService } from '../../../core/services/account.service';
import { ErrorMessageComponent } from '../../../shared/components/error-message/error-message.component';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-open-account',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, MatCardModule, MatFormFieldModule, MatSelectModule, MatButtonModule, MatIconModule, ErrorMessageComponent],
  templateUrl: './open-account.component.html',
  styleUrls: ['./open-account.component.scss']
})
export class OpenAccountComponent {
  form: FormGroup;
  loading = false;
  error = '';

  constructor(private fb: FormBuilder, private accountService: AccountService, private router: Router) {
    this.form = this.fb.group({ currency: ['BRL', Validators.required] });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.loading = true; this.error = '';
    this.accountService.openAccount(this.form.value).subscribe({
      next: account => this.router.navigate(['/accounts', account.id]),
      error: (err: HttpErrorResponse) => { this.loading = false; this.error = err.error?.detail ?? 'Erro ao abrir conta.'; }
    });
  }
}
