package com.nexbank.adapters.in.web.dto;

import com.nexbank.domain.account.AccountStatus;
import com.nexbank.domain.shared.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse(
    UUID id,
    String accountNumber,
    Currency currency,
    BigDecimal balance,
    AccountStatus status,
    Instant openedAt
) {}
