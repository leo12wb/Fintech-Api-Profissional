package com.nexbank.application.account.port.in;

import com.nexbank.domain.shared.Currency;
import java.util.UUID;

public record OpenAccountCommand(UUID customerId, Currency currency) {
    public OpenAccountCommand {
        if (customerId == null) throw new IllegalArgumentException("customerId required");
        if (currency == null) throw new IllegalArgumentException("currency required");
    }
}
