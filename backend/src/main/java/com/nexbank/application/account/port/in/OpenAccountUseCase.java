package com.nexbank.application.account.port.in;

import com.nexbank.domain.account.Account;

public interface OpenAccountUseCase {
    Account openAccount(OpenAccountCommand command);
}
