package com.nexbank.application.transaction.port.out;

import com.nexbank.application.shared.PagedResult;
import com.nexbank.domain.transaction.FinancialTransaction;
import com.nexbank.domain.transaction.TransactionType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    FinancialTransaction save(FinancialTransaction transaction);
    Optional<FinancialTransaction> findById(UUID id);
    Optional<FinancialTransaction> findByIdempotencyKey(String key);
    PagedResult<FinancialTransaction> findByAccountId(UUID accountId, Instant from, Instant to, TransactionType type, int page, int size);
    long count();
}
