package com.nexbank.adapters.in.web.dto;

import com.nexbank.domain.shared.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositRequest(
    @NotNull UUID accountId,
    @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
    @NotNull Currency currency
) {}
