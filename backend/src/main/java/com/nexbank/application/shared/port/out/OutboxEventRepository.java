package com.nexbank.application.shared.port.out;

public interface OutboxEventRepository {
    void save(String aggregateType, String aggregateId, String eventType, String payloadJson);
}
