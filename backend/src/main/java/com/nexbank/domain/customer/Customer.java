package com.nexbank.domain.customer;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class Customer {
    private final UUID id;
    private final String name;
    private final String email;        // stored normalized (lowercase, trimmed)
    private final String loginId;
    private final String passwordHash;
    private final CustomerRole role;
    private final CustomerStatus status;
    private final Instant createdAt;
    private final Instant updatedAt;

    public boolean isActive() { return CustomerStatus.ACTIVE.equals(status); }

    public static String normalizeEmail(String email) {
        if (email == null) throw new IllegalArgumentException("Email must not be null");
        return email.strip().toLowerCase();
    }
}
