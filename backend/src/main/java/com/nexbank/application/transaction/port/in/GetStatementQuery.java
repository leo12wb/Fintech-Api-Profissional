package com.nexbank.application.transaction.port.in;

import com.nexbank.domain.transaction.TransactionType;
import java.time.Instant;
import java.util.UUID;

public record GetStatementQuery(
    UUID customerId,
    UUID accountId,
    Instant from,
    Instant to,
    TransactionType type,
    int page,
    int size
) {}
