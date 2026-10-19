package com.nexbank.adapters.out.configuration;

import com.nexbank.adapters.out.persistence.repository.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("outbox")
@RequiredArgsConstructor
public class OutboxHealthIndicator implements HealthIndicator {

    private static final long DEGRADED_THRESHOLD = 50;
    private static final long CRITICAL_THRESHOLD  = 200;

    private final OutboxEventJpaRepository outboxRepository;

    @Override
    public Health health() {
        long pending = outboxRepository.countPendingEvents();
        long failed  = outboxRepository.countFailedEvents();

        if (failed > 0 || pending >= CRITICAL_THRESHOLD) {
            return Health.down()
                .withDetail("pendingEvents", pending)
                .withDetail("failedEvents", failed)
                .build();
        }
        if (pending >= DEGRADED_THRESHOLD) {
            return Health.status("DEGRADED")
                .withDetail("pendingEvents", pending)
                .withDetail("failedEvents", failed)
                .build();
        }
        return Health.up()
            .withDetail("pendingEvents", pending)
            .withDetail("failedEvents", failed)
            .build();
    }
}
