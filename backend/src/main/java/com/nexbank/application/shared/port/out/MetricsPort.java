package com.nexbank.application.shared.port.out;

import com.nexbank.domain.shared.Money;

public interface MetricsPort {
    void recordDeposit(Money amount);
    void recordTransfer(Money amount);
}
