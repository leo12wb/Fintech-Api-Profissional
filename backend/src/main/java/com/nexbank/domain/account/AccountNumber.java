package com.nexbank.domain.account;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class AccountNumber {
    private final String value;

    private AccountNumber(String value) { this.value = value; }

    public static AccountNumber of(String value) {
        Objects.requireNonNull(value);
        if (!value.matches("\\d{10}")) throw new IllegalArgumentException("Invalid account number format");
        return new AccountNumber(value);
    }

    public static AccountNumber generate() {
        long number = ThreadLocalRandom.current().nextLong(1_000_000_000L, 9_999_999_999L);
        return new AccountNumber(String.valueOf(number));
    }

    public String getValue() { return value; }

    @Override public boolean equals(Object o) {
        if (!(o instanceof AccountNumber a)) return false;
        return value.equals(a.value);
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value; }
}
