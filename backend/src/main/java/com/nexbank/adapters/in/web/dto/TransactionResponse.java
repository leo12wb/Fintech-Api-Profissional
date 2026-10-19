package com.nexbank.adapters.in.web.dto;

import com.nexbank.domain.shared.Currency;
import com.nexbank.domain.transaction.TransactionStatus;
import com.nexbank.domain.transaction.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
    UUID id,
    TransactionType type,
    UUID sourceAccountId,
    UUID destinationAccountId,
    BigDecimal amount,
    Currency currency,
    TransactionStatus status,
    String idempotencyKey,
    Instant createdAt
) {}
