package com.nexbank.adapters.messaging;

import com.nexbank.adapters.out.configuration.RabbitMqConfig;
import com.nexbank.adapters.out.messaging.OutboxPoller;
import com.nexbank.adapters.out.persistence.entity.OutboxEventJpaEntity;
import com.nexbank.adapters.out.persistence.repository.OutboxEventJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPollerTest {

    @Mock private OutboxEventJpaRepository outboxRepository;
    @Mock private RabbitTemplate rabbitTemplate;
    @InjectMocks private OutboxPoller poller;

    private OutboxEventJpaEntity buildEvent(String eventType) {
        OutboxEventJpaEntity e = new OutboxEventJpaEntity();
        e.setId(UUID.randomUUID());
        e.setAggregateType("Transaction");
        e.setAggregateId(UUID.randomUUID().toString());
        e.setEventType(eventType);
        e.setPayload("{\"transactionId\":\"abc\"}");
        e.setStatus("PENDING");
        e.setCreatedAt(Instant.now());
        e.setRetryCount(0);
        return e;
    }

    @Test
    void shouldPublishPendingEventAndMarkProcessed() {
        OutboxEventJpaEntity event = buildEvent("TransactionCompleted");
        when(outboxRepository.findPendingEvents(any(PageRequest.class))).thenReturn(List.of(event));

        poller.pollAndPublish();

        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMqConfig.EXCHANGE),
            eq(RabbitMqConfig.ROUTING_TRANSACTION_COMPLETED),
            eq(event.getPayload())
        );
        assertThat(event.getStatus()).isEqualTo("PROCESSED");
        assertThat(event.getProcessedAt()).isNotNull();
        verify(outboxRepository).save(event);
    }

    @Test
    void shouldIncrementRetryCountOnFailure() {
        OutboxEventJpaEntity event = buildEvent("TransactionCompleted");
        when(outboxRepository.findPendingEvents(any(PageRequest.class))).thenReturn(List.of(event));
        doThrow(new AmqpException("broker down"))
            .when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

        poller.pollAndPublish();

        assertThat(event.getStatus()).isEqualTo("PENDING");
        assertThat(event.getRetryCount()).isEqualTo(1);
        verify(outboxRepository).save(event);
    }

    @Test
    void shouldMarkAsFailedAfterMaxRetries() {
        OutboxEventJpaEntity event = buildEvent("TransactionCompleted");
        event.setRetryCount(2); // next failure = 3rd = MAX
        when(outboxRepository.findPendingEvents(any(PageRequest.class))).thenReturn(List.of(event));
        doThrow(new AmqpException("broker down"))
            .when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

        poller.pollAndPublish();

        assertThat(event.getStatus()).isEqualTo("FAILED");
        assertThat(event.getRetryCount()).isEqualTo(3);
    }

    @Test
    void shouldSkipPollWhenNoPendingEvents() {
        when(outboxRepository.findPendingEvents(any(PageRequest.class))).thenReturn(List.of());

        poller.pollAndPublish();

        verifyNoInteractions(rabbitTemplate);
        verify(outboxRepository, never()).save(any());
    }

    @Test
    void shouldUseCorrectRoutingKeyForAccountCreated() {
        OutboxEventJpaEntity event = buildEvent("AccountCreated");
        when(outboxRepository.findPendingEvents(any(PageRequest.class))).thenReturn(List.of(event));

        poller.pollAndPublish();

        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMqConfig.EXCHANGE),
            eq(RabbitMqConfig.ROUTING_ACCOUNT_CREATED),
            any(Object.class)
        );
    }
}
