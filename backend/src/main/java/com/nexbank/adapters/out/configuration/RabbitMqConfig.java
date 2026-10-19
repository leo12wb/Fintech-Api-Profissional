package com.nexbank.adapters.out.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "nexbank.events";

    public static final String TRANSACTION_COMPLETED_QUEUE = "nexbank.transaction.completed";
    public static final String ACCOUNT_CREATED_QUEUE        = "nexbank.account.created";
    public static final String TRANSACTION_COMPLETED_DLQ    = "nexbank.transaction.completed.dlq";

    public static final String ROUTING_TRANSACTION_COMPLETED = "transaction.completed";
    public static final String ROUTING_ACCOUNT_CREATED       = "account.created";

    @Bean
    public TopicExchange nexbankExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue transactionCompletedQueue() {
        return QueueBuilder.durable(TRANSACTION_COMPLETED_QUEUE)
            .withArgument("x-dead-letter-exchange", "")
            .withArgument("x-dead-letter-routing-key", TRANSACTION_COMPLETED_DLQ)
            .build();
    }

    @Bean
    public Queue transactionCompletedDlq() {
        return QueueBuilder.durable(TRANSACTION_COMPLETED_DLQ).build();
    }

    @Bean
    public Queue accountCreatedQueue() {
        return QueueBuilder.durable(ACCOUNT_CREATED_QUEUE).build();
    }

    @Bean
    public Binding transactionCompletedBinding(Queue transactionCompletedQueue, TopicExchange nexbankExchange) {
        return BindingBuilder.bind(transactionCompletedQueue).to(nexbankExchange).with(ROUTING_TRANSACTION_COMPLETED);
    }

    @Bean
    public Binding accountCreatedBinding(Queue accountCreatedQueue, TopicExchange nexbankExchange) {
        return BindingBuilder.bind(accountCreatedQueue).to(nexbankExchange).with(ROUTING_ACCOUNT_CREATED);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
