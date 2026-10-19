package com.nexbank.application.transaction.port.in;

import com.nexbank.domain.shared.Money;
import java.util.UUID;

public record TransferCommand(
    UUID customerId,
    UUID sourceAccountId,
    UUID destinationAccountId,
    Money amount,
    String idempotencyKey
) {
    public TransferCommand {
        if (customerId == null) throw new IllegalArgumentException("customerId required");
        if (sourceAccountId == null) throw new IllegalArgumentException("sourceAccountId required");
        if (destinationAccountId == null) throw new IllegalArgumentException("destinationAccountId required");
        if (sourceAccountId.equals(destinationAccountId))
            throw new IllegalArgumentException("Source and destination accounts must be different");
        if (amount == null || amount.isZero()) throw new IllegalArgumentException("amount must be positive");
        if (idempotencyKey == null || idempotencyKey.isBlank()) throw new IllegalArgumentException("idempotencyKey required");
    }
}
