package com.cqrs.example.taskmanager.api.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventbus.kafka.KafkaConfig;
import com.cqrs.eventbus.kafka.KafkaEventPublisher;
import com.cqrs.messaging.JsonDeserializer;
import com.cqrs.messaging.JsonSerializer;

@Configuration
public class EventPublisherConfiguration {

    private final KafkaConfig config;

    public EventPublisherConfiguration(Environment environment) {
        config = new KafkaConfig(
            environment.getProperty("kafka.bootstrap.servers", "localhost:9092"),
            environment.getProperty("kafka.group.id", "task-manager"),
            environment.getProperty("kafka.consumer.threads", int.class, 4)
        );
    }

    /*
     * Create the Event publisher.
     */
    @Bean
    public EventPublisher eventPublisher() {
        return new KafkaEventPublisher(config, new JsonSerializer(), new JsonDeserializer());
    }
}
