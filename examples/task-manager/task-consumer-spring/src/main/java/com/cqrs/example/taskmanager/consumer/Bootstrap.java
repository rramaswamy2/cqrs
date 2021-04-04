package com.cqrs.example.taskmanager.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.cqrs.eventbus.kafka.KafkaConfig;
import com.cqrs.eventbus.kafka.KafkaEventConsumer;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.JsonDeserializer;
import com.cqrs.messaging.JsonSerializer;

@Component
public class Bootstrap implements CommandLineRunner, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(Bootstrap.class);

    private final Environment environment;
    private final ActionHandlerResolver actionHandlerResolver;
    private KafkaEventConsumer consumer;

    public Bootstrap(Environment environment, ActionHandlerResolver actionHandlerResolver) {
        this.environment = environment;
        this.actionHandlerResolver = actionHandlerResolver;
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("Bootstrapping Kafka consumer");
        //TODO: check for commandline arguments for overrides of settings and update the config read from application.properties
        KafkaConfig config = new KafkaConfig(
            environment.getProperty("kafka.bootstrap.servers", "localhost:9092"),
            environment.getProperty("kafka.group.id", "task-manager"),
            environment.getProperty("kafka.consumer.threads", int.class, 4)
        );
        consumer = new KafkaEventConsumer(config, new JsonSerializer(), new JsonDeserializer(), actionHandlerResolver);
        consumer.start("Task");
    }

    @Override
    public void close() throws Exception {
        if (consumer != null) {
            consumer.close();
        }
    }
}
