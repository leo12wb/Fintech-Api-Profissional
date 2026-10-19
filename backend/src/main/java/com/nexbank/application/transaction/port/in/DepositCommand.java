package com.nexbank.application.transaction.port.in;

import com.nexbank.domain.shared.Money;
import java.util.UUID;

public record DepositCommand(
    UUID customerId,
    UUID accountId,
    Money amount,
    String idempotencyKey
) {
    public DepositCommand {
        if (customerId == null) throw new IllegalArgumentException("customerId required");
        if (accountId == null) throw new IllegalArgumentException("accountId required");
        if (amount == null || amount.isZero()) throw new IllegalArgumentException("amount must be positive");
        if (idempotencyKey == null || idempotencyKey.isBlank()) throw new IllegalArgumentException("idempotencyKey required");
    }
}
