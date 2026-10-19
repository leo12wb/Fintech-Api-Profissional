package com.nexbank.adapters.in.web.dto;

import com.nexbank.domain.shared.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
    @NotNull UUID sourceAccountId,
    @NotBlank String destinationAccountNumber,
    @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
    @NotNull Currency currency
) {}
