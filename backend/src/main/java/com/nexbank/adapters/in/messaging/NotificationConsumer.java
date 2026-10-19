package com.nexbank.adapters.in.messaging;

import com.nexbank.adapters.out.configuration.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationConsumer {

    @RabbitListener(queues = RabbitMqConfig.TRANSACTION_COMPLETED_QUEUE)
    public void onTransactionCompleted(String payload) {
        log.info("[NOTIFICATION] TransactionCompleted received: {}", payload);
        // Production: call notification gateway (push/email/SMS)
    }

    @RabbitListener(queues = RabbitMqConfig.ACCOUNT_CREATED_QUEUE)
    public void onAccountCreated(String payload) {
        log.info("[NOTIFICATION] AccountCreated received: {}", payload);
        // Production: send welcome email
    }
}
