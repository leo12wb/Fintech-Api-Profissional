package com.nexbank.domain.account;

import com.nexbank.domain.shared.Currency;
import com.nexbank.domain.shared.Money;
import com.nexbank.domain.shared.exception.BusinessRuleException;
import lombok.Builder;
import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class Account {
    private final UUID id;
    private final AccountNumber accountNumber;
    private final UUID customerId;
    private final Currency currency;
    private final Money balance;
    private final AccountStatus status;
    private final Instant openedAt;
    private final Instant updatedAt;
    private final Long version;

    public boolean isActive() { return AccountStatus.ACTIVE.equals(status); }

    public void assertActive() {
        if (!isActive()) {
            throw new BusinessRuleException("ACCOUNT_NOT_ACTIVE",
                "Account " + accountNumber.getValue() + " is not active. Status: " + status);
        }
    }

    public Account credit(Money amount) {
        assertActive();
        assertSameCurrency(amount);
        return toBuilder().balance(balance.add(amount)).updatedAt(Instant.now()).build();
    }

    public Account debit(Money amount) {
        assertActive();
        assertSameCurrency(amount);
        if (!balance.isGreaterThanOrEqualTo(amount)) {
            throw new BusinessRuleException("INSUFFICIENT_FUNDS",
                "Insufficient funds. Available: " + balance + ", required: " + amount);
        }
        return toBuilder().balance(balance.subtract(amount)).updatedAt(Instant.now()).build();
    }

    private void assertSameCurrency(Money amount) {
        if (!currency.equals(amount.getCurrency())) {
            throw new BusinessRuleException("CURRENCY_MISMATCH",
                "Account currency " + currency + " does not match transaction currency " + amount.getCurrency());
        }
    }

    public boolean belongsTo(UUID customerId) {
        return this.customerId.equals(customerId);
    }
}
