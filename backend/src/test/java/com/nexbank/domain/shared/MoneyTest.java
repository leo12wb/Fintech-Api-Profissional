package com.nexbank.domain.shared;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

class MoneyTest {
    @Test void shouldCreateMoneyWithCorrectScale() {
        Money m = Money.of(new BigDecimal("10.5"), Currency.BRL);
        assertThat(m.getAmount()).isEqualByComparingTo(new BigDecimal("10.50"));
    }
    @Test void shouldAddSameCurrency() {
        Money a = Money.of(new BigDecimal("10.00"), Currency.BRL);
        Money b = Money.of(new BigDecimal("5.50"), Currency.BRL);
        assertThat(a.add(b)).isEqualTo(Money.of(new BigDecimal("15.50"), Currency.BRL));
    }
    @Test void shouldRejectNegativeAmount() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("-1"), Currency.BRL))
            .isInstanceOf(IllegalArgumentException.class);
    }
    @Test void shouldRejectDifferentCurrenciesOnAdd() {
        Money brl = Money.of(BigDecimal.TEN, Currency.BRL);
        Money usd = Money.of(BigDecimal.TEN, Currency.USD);
        assertThatThrownBy(() -> brl.add(usd)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void shouldConvertToCentsAndBack() {
        Money m = Money.of(new BigDecimal("123.45"), Currency.BRL);
        assertThat(m.toCents()).isEqualTo(12345L);
        assertThat(Money.ofCents(12345L, Currency.BRL)).isEqualTo(m);
    }
    @Test void shouldSubtractCorrectly() {
        Money a = Money.of(new BigDecimal("10.00"), Currency.BRL);
        Money b = Money.of(new BigDecimal("3.00"), Currency.BRL);
        assertThat(a.subtract(b)).isEqualTo(Money.of(new BigDecimal("7.00"), Currency.BRL));
    }
    @Test void shouldRejectSubtractResultingNegative() {
        Money a = Money.of(new BigDecimal("3.00"), Currency.BRL);
        Money b = Money.of(new BigDecimal("10.00"), Currency.BRL);
        assertThatThrownBy(() -> a.subtract(b)).isInstanceOf(IllegalArgumentException.class);
    }
}
