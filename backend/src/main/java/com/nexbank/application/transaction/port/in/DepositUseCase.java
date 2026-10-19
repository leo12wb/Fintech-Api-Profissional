package com.nexbank.application.transaction.port.in;

import com.nexbank.domain.transaction.FinancialTransaction;

public interface DepositUseCase {
    FinancialTransaction deposit(DepositCommand command);
}
