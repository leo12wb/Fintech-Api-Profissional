package com.nexbank.adapters.in.web.dto;

import com.nexbank.domain.account.AccountStatus;
import com.nexbank.domain.shared.Currency;

import java.math.BigDecimal;

public record BalanceResponse(
    String accountNumber,
    BigDecimal balance,
    Currency currency,
    AccountStatus status
) {}
