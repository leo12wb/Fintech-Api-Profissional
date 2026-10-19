package com.nexbank.application.account.port.out;

import com.nexbank.domain.account.Account;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {
    Account save(Account account);
    Optional<Account> findById(UUID id);
    Optional<Account> findByIdWithLock(UUID id); // for pessimistic locking during transfers
    List<Account> findByCustomerId(UUID customerId);
    boolean existsByAccountNumber(String accountNumber);
}
