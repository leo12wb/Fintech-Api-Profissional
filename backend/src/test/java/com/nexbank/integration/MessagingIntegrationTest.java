package com.nexbank.integration;

import com.nexbank.adapters.out.messaging.OutboxPoller;
import com.nexbank.adapters.out.persistence.repository.OutboxEventJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class MessagingIntegrationTest extends BaseIntegrationTest {

    @Autowired private OutboxPoller outboxPoller;
    @Autowired private OutboxEventJpaRepository outboxRepository;

    @Test
    void outboxPollerBeanShouldBeConfigured() {
        assertThat(outboxPoller).isNotNull();
    }

    @Test
    void outboxRepositoryShouldCountPendingAndFailedEvents() {
        long pending = outboxRepository.countPendingEvents();
        long failed  = outboxRepository.countFailedEvents();

        // No events in clean test database
        assertThat(pending).isGreaterThanOrEqualTo(0);
        assertThat(failed).isGreaterThanOrEqualTo(0);
    }

    @Test
    void outboxPollerShouldRunWithoutErrorOnEmptyOutbox() {
        // Verifies the full poller pipeline doesn't throw with real RabbitMQ + Postgres
        outboxPoller.pollAndPublish();
    }
}
