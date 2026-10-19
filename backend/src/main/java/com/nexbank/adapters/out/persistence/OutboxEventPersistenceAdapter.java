package com.nexbank.adapters.out.persistence;

import com.nexbank.adapters.out.persistence.entity.OutboxEventJpaEntity;
import com.nexbank.adapters.out.persistence.repository.OutboxEventJpaRepository;
import com.nexbank.application.shared.port.out.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventPersistenceAdapter implements OutboxEventRepository {

    private final OutboxEventJpaRepository jpaRepository;

    @Override
    public void save(String aggregateType, String aggregateId, String eventType, String payloadJson) {
        OutboxEventJpaEntity entity = OutboxEventJpaEntity.builder()
            .id(UUID.randomUUID())
            .aggregateType(aggregateType)
            .aggregateId(aggregateId)
            .eventType(eventType)
            .payload(payloadJson)
            .status("PENDING")
            .createdAt(Instant.now())
            .retryCount(0)
            .build();
        jpaRepository.save(entity);
    }
}
