package com.nexbank.application.account.port.in;

import com.nexbank.domain.account.Account;
import java.util.List;
import java.util.UUID;

public interface GetAccountUseCase {
    Account getAccountById(UUID accountId, UUID requestingCustomerId);
    List<Account> getAccountsByCustomer(UUID customerId);
}
