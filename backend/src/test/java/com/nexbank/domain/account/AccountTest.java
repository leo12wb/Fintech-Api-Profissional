package com.nexbank.domain.account;

import com.nexbank.domain.shared.Currency;
import com.nexbank.domain.shared.Money;
import com.nexbank.domain.shared.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

class AccountTest {

    private Account activeAccount(Money balance) {
        return Account.builder()
            .id(UUID.randomUUID())
            .accountNumber(AccountNumber.of("1234567890"))
            .customerId(UUID.randomUUID())
            .currency(Currency.BRL)
            .balance(balance)
            .status(AccountStatus.ACTIVE)
            .openedAt(Instant.now())
            .updatedAt(Instant.now())
            .version(0L)
            .build();
    }

    @Test void shouldCreditAccount() {
        Account account = activeAccount(Money.of(new BigDecimal("100.00"), Currency.BRL));
        Account credited = account.credit(Money.of(new BigDecimal("50.00"), Currency.BRL));
        assertThat(credited.getBalance()).isEqualTo(Money.of(new BigDecimal("150.00"), Currency.BRL));
    }

    @Test void shouldDebitAccount() {
        Account account = activeAccount(Money.of(new BigDecimal("100.00"), Currency.BRL));
        Account debited = account.debit(Money.of(new BigDecimal("40.00"), Currency.BRL));
        assertThat(debited.getBalance()).isEqualTo(Money.of(new BigDecimal("60.00"), Currency.BRL));
    }

    @Test void shouldRejectDebitWhenInsufficientFunds() {
        Account account = activeAccount(Money.of(new BigDecimal("10.00"), Currency.BRL));
        assertThatThrownBy(() -> account.debit(Money.of(new BigDecimal("50.00"), Currency.BRL)))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("Insufficient funds");
    }

    @Test void shouldRejectOperationOnBlockedAccount() {
        Account blocked = Account.builder()
            .id(UUID.randomUUID()).accountNumber(AccountNumber.of("1234567890"))
            .customerId(UUID.randomUUID()).currency(Currency.BRL)
            .balance(Money.of(BigDecimal.TEN, Currency.BRL))
            .status(AccountStatus.BLOCKED)
            .openedAt(Instant.now()).updatedAt(Instant.now()).version(0L).build();
        assertThatThrownBy(() -> blocked.credit(Money.of(BigDecimal.ONE, Currency.BRL)))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("not active");
    }

    @Test void shouldRejectCurrencyMismatch() {
        Account account = activeAccount(Money.of(BigDecimal.TEN, Currency.BRL));
        assertThatThrownBy(() -> account.credit(Money.of(BigDecimal.ONE, Currency.USD)))
            .isInstanceOf(BusinessRuleException.class)
            .hasMessageContaining("currency");
    }
}
