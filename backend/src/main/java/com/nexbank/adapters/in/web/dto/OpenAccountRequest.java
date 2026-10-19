package com.nexbank.adapters.in.web.dto;

import com.nexbank.domain.shared.Currency;
import jakarta.validation.constraints.NotNull;

public record OpenAccountRequest(@NotNull Currency currency) {}
