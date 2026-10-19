package com.nexbank.adapters.in.web.dto;

import com.nexbank.domain.customer.CustomerRole;
import com.nexbank.domain.customer.CustomerStatus;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
    UUID id,
    String name,
    String email,
    CustomerRole role,
    CustomerStatus status,
    Instant createdAt
) {}
