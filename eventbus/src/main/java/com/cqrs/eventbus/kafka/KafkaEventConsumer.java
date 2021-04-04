package com.cqrs.eventbus.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqrs.eventbus.serialize.EventEnvelope;
import com.cqrs.messaging.ActionHandler;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.Deserializer;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.Serializer;

public class KafkaEventConsumer implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaEventConsumer.class);
    private final ExecutorService executor;
    private KafkaConfig config;
    private final ActionHandlerResolver resolver;
    private final Serializer serializer;
    private final Deserializer deserializer;
    private final List<ConsumerLoop> consumers;

    private class ConsumerLoop implements Runnable {
        private final int id;
        private final Serializer serializer;
        private final Deserializer deserializer;
        private final List<String> topics;
        private final KafkaConsumer<String, String> consumer;

        ConsumerLoop(int id, KafkaConfig config, Serializer serializer, Deserializer deserializer,
                List<String> topics) {
            this.id = id;
            this.serializer = serializer;
            this.deserializer = deserializer;
            this.topics = topics;

            Properties props = new Properties();
            props.put("bootstrap.servers", config.getBootstrapServers());
            props.put("group.id", config.getGroupId());
            props.put("key.deserializer", StringDeserializer.class.getName());
            props.put("value.deserializer", StringDeserializer.class.getName());
            props.put("session.timeout.ms", "60000");
            props.put("enable.auto.commit", "false");
            this.consumer = new KafkaConsumer<>(props);
        }

        @Override
        public void run() {
            try {
                consumer.subscribe(topics);
                LOG.info("ConsumerLoop ID: {}, Consumer thread ID: {} ,subscribed topics {}", id,
                        Thread.currentThread().getId(), topics);

                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                    LOG.debug("Thread ID : {} polled consumer record topic partition size : {} ",
                            Thread.currentThread().getId(), records.partitions().size());

                    records.partitions().forEach(p -> {
                        LOG.debug("Thread ID: {} , consumer record size {} for topic partition {}",
                                Thread.currentThread().getId(), records.records(p).size(),
                                p.topic() + "-" + p.partition());
                        processPartitionRecords(p, records.records(p));
                    });
                }
            } catch (WakeupException e) {
                // ignore for shutdown
                LOG.debug("Thread ID {} wakeup and closing consumer..", Thread.currentThread().getId());
            } finally {
                consumer.close();
            }
        }

        private void processPartitionRecords(TopicPartition partition,
                List<ConsumerRecord<String, String>> partitionRecords) {
            for (ConsumerRecord<String, String> record : partitionRecords) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                            "ConsumerLoop ID: {}, consumer Thread {}, record key {}, record value {}, offset {} , retrieved from topic {} partition {} , record timestamp {}",
                            id, Thread.currentThread().getId(), record.key(), record.value(), record.offset(),
                            record.topic(), record.partition(), record.timestamp());
                    LOG.debug("Consumer Thread {} Consumer Record string  {} ", Thread.currentThread().getId(),
                            record.toString());
                }
                tryHandleAction(record.value());

            }

            long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
            consumer.commitAsync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)),
                    (offsets, exception) -> {
                        if (exception != null) {
                            LOG.error("Failed to commit consumer offset: ", exception);
                        }
                    });
        }

        private void tryHandleAction(String message) {
            try {
                handleAction(message);
            } catch (Exception ex) {
                handleException(ex);
            }
        }

        private void handleAction(String value) {
            EventEnvelope eventClazz = this.deserializer.deserialize(value, EventEnvelope.class);
            List<ActionHandler> handlers = resolver.findHandlersFor(eventClazz.eventType);
            Class<?> clazz = resolver.getHandledActionType(handlers.get(0).getClass());
            Event event = (Event) this.deserializer.deserialize(this.serializer.serialize(eventClazz.eventData), clazz);
            handlers.forEach(handler -> handler.handle(event));
        }

        private void handleException(Exception ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Application error while handling the action: ", ex);
            }
        }

        public void shutdown() {
            consumer.wakeup();
        }
    }

    public KafkaEventConsumer(KafkaConfig config, Serializer serializer, Deserializer deserializer,
            ActionHandlerResolver resolver) {
        this.config = config;
        this.resolver = resolver;
        this.deserializer = deserializer;
        this.serializer = serializer;
        this.executor = Executors.newFixedThreadPool(config.getConcurrency());
        this.consumers = new ArrayList<>(config.getConcurrency());

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownConsumerThreads));
    }

    public void start(String topic) {
        LOG.debug("starting kafka consumer with {} threads ", config.getConcurrency());
        for (int i = 0; i < config.getConcurrency(); i++) {
            ConsumerLoop consumer = new ConsumerLoop(i, this.config, this.serializer, this.deserializer,
                    Collections.singletonList(topic));
            consumers.add(consumer);
            executor.submit(consumer);
        }
    }

    @Override
    public void close() throws Exception {
        shutdownConsumerThreads();
    }

    private void shutdownConsumerThreads() {
        for (ConsumerLoop consumer : consumers) {
            consumer.shutdown();
        }
        executor.shutdown();
        try {
            executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOG.error("Failed to terminate consumer threads: ", e);
            Thread.currentThread().interrupt();
        }
    }

}
