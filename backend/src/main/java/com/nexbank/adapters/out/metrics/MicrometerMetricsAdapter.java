package com.nexbank.adapters.out.metrics;

import com.nexbank.application.shared.port.out.MetricsPort;
import com.nexbank.domain.shared.Money;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MicrometerMetricsAdapter implements MetricsPort {

    private final Counter depositCounter;
    private final Counter transferCounter;
    private final DistributionSummary depositAmountSummary;
    private final DistributionSummary transferAmountSummary;

    public MicrometerMetricsAdapter(MeterRegistry registry) {
        this.depositCounter = Counter.builder("nexbank_transactions_total")
            .tag("type", "deposit")
            .description("Total number of completed deposits")
            .register(registry);

        this.transferCounter = Counter.builder("nexbank_transactions_total")
            .tag("type", "transfer")
            .description("Total number of completed transfers")
            .register(registry);

        this.depositAmountSummary = DistributionSummary.builder("nexbank_transaction_amount_cents")
            .tag("type", "deposit")
            .description("Deposit amounts in cents")
            .baseUnit("cents")
            .register(registry);

        this.transferAmountSummary = DistributionSummary.builder("nexbank_transaction_amount_cents")
            .tag("type", "transfer")
            .description("Transfer amounts in cents")
            .baseUnit("cents")
            .register(registry);
    }

    @Override
    public void recordDeposit(Money amount) {
        depositCounter.increment();
        depositAmountSummary.record(amount.toCents());
    }

    @Override
    public void recordTransfer(Money amount) {
        transferCounter.increment();
        transferAmountSummary.record(amount.toCents());
    }
}
