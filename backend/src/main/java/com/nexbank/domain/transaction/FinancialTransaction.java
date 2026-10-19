package com.nexbank.domain.transaction;

import com.nexbank.domain.shared.Money;
import lombok.Builder;
import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class FinancialTransaction {
    private final UUID id;
    private final TransactionType type;
    private final UUID sourceAccountId;      // null for deposits
    private final UUID destinationAccountId; // null for withdrawals (not implemented)
    private final Money amount;
    private final TransactionStatus status;
    private final String idempotencyKey;
    private final String businessReference;
    private final Instant createdAt;
}
