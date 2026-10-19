package com.nexbank.adapters.out.messaging;

import com.nexbank.adapters.out.configuration.RabbitMqConfig;
import com.nexbank.adapters.out.persistence.entity.OutboxEventJpaEntity;
import com.nexbank.adapters.out.persistence.repository.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPoller {

    private static final int BATCH_SIZE = 50;
    private static final int MAX_RETRIES = 3;

    private static final Map<String, String> ROUTING_KEYS = Map.of(
        "TransactionCompleted", RabbitMqConfig.ROUTING_TRANSACTION_COMPLETED,
        "AccountCreated",       RabbitMqConfig.ROUTING_ACCOUNT_CREATED
    );

    private final OutboxEventJpaRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelayString = "${nexbank.messaging.outbox-poll-interval-ms:5000}")
    @Transactional
    public void pollAndPublish() {
        List<OutboxEventJpaEntity> pending =
            outboxRepository.findPendingEvents(PageRequest.of(0, BATCH_SIZE));

        if (pending.isEmpty()) {
            return;
        }

        log.debug("Outbox poller: processing {} pending events", pending.size());

        for (OutboxEventJpaEntity event : pending) {
            publishEvent(event);
        }
    }

    private void publishEvent(OutboxEventJpaEntity event) {
        String routingKey = ROUTING_KEYS.getOrDefault(
            event.getEventType(), event.getEventType().toLowerCase().replace("_", "."));

        try {
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, routingKey, event.getPayload());
            event.setStatus("PROCESSED");
            event.setProcessedAt(Instant.now());
            log.info("Outbox event published: id={} type={} routingKey={}",
                event.getId(), event.getEventType(), routingKey);
        } catch (AmqpException ex) {
            int retries = event.getRetryCount() + 1;
            event.setRetryCount(retries);
            if (retries >= MAX_RETRIES) {
                event.setStatus("FAILED");
                log.error("Outbox event permanently failed after {} retries: id={} type={}",
                    MAX_RETRIES, event.getId(), event.getEventType(), ex);
            } else {
                log.warn("Outbox event publish failed (retry {}/{}): id={} type={}",
                    retries, MAX_RETRIES, event.getId(), event.getEventType(), ex);
            }
        }
        outboxRepository.save(event);
    }
}
