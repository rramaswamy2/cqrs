package com.cqrs.eventbus.kafka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventbus.events.TestEvent;
import com.cqrs.eventbus.kafka.KafkaConfig;
import com.cqrs.eventbus.kafka.KafkaEventConsumer;
import com.cqrs.eventbus.kafka.KafkaEventPublisher;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.Deserializer;
import com.cqrs.messaging.EventHandler;
import com.cqrs.messaging.ID;
import com.cqrs.messaging.JsonDeserializer;
import com.cqrs.messaging.JsonSerializer;
import com.cqrs.messaging.Serializer;

import static com.cqrs.assertj.Assertions.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class WhenPublishingMultipleEventsToMultipleConsumersTest implements EventHandler<TestEvent> {

    private static Map<ID, TestEvent> toBePublished = new HashMap<>();
    private final KafkaEventConsumer consumer;
    private static Map<ID, TestEvent> publishedEvent = new HashMap<>();

    @Override
    public void handle(TestEvent event) {
        System.out.println(String.format("Handling event with stream_id %s by Thread %s", event.getStreamId(), Thread.currentThread().getName()));
        publishedEvent.put(event.getStreamId(), event);
    }

    public WhenPublishingMultipleEventsToMultipleConsumersTest() throws Exception {
        // testing with 3 kafka consumer threads and 8 partitions for the kafka configuration
        KafkaConfig config = new KafkaConfig("localhost:9092", "cqrs-kafka-eventbus-test", 3, 8);
        Deserializer deserializer = new JsonDeserializer();
        Serializer serializer = new JsonSerializer();
        ActionHandlerResolver resolver = new ActionHandlerResolver();
        resolver.registerActionHandler(this);

        for(int i=1; i<= 10; i++) {
        ID idnext = ID.fromObject(UUID.randomUUID());
        toBePublished.put(idnext, new TestEvent(idnext, "Awesomeness has happened "+i, i));
        
        }
       
        KafkaEventPublisher publisher = new KafkaEventPublisher(config, serializer, deserializer);
        System.out.println("number of partitions for topic : " + config.getNumPartitions());
        publisher.createTopicWithMultiplePartitions("TestKafkaTopic4", config.getNumPartitions(), 1);
        
        System.out.println("new topics " + publisher.listTopics());
        
        await().atLeast(1, SECONDS).pollDelay(1, SECONDS).until(() -> true);
        
        consumer = new KafkaEventConsumer(config, serializer, deserializer, resolver);
        consumer.start("TestKafkaTopic4");
        
        // First time topic consumer group creation takes bit of time in kafka (~3-4 seconds)
        await().atLeast(6, SECONDS).pollDelay(6, SECONDS).until(() -> true);
        
        publisher.publish("TestKafkaTopic4", new ArrayList<>(toBePublished.values()));
        await().atMost(10, SECONDS).until(() -> publishedEvent.size() == 10);
    }

    @Test
    public void testItShouldPublishTheEventToTheRegisteredEventHandler() {
        assertNotNull(publishedEvent, "The published event is not received in the registered handler");
        assertEquals(10, publishedEvent.size(), "The published event is not received in the registered handler");
    }

    @Test
    public void testItShouldHaveTheSameValuesForProperties() {
        for (Map.Entry<ID, TestEvent> toBePublishedEntry : toBePublished.entrySet()) {
            TestEvent toBePublishedTestEvent = toBePublishedEntry.getValue();
            TestEvent publishedTestEvent = publishedEvent.get(toBePublishedEntry.getKey());

            assertThat(publishedTestEvent).isNotNull()
                .hasStreamId(toBePublishedTestEvent.getStreamId())
                .hasEventId()
                .hasVersion(toBePublishedTestEvent.getVersion());

            assertEquals(toBePublishedTestEvent.getStuff(), publishedTestEvent.getStuff(),
                "The published event stuff value has changed");
        }
    }

    @AfterAll
    private void tearDown() {
        try {
            consumer.close();
        } catch (Exception e) {

        }
    }
}
