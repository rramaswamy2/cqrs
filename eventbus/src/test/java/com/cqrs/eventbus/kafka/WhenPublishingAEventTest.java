package com.cqrs.eventbus.kafka;

import static com.cqrs.assertj.Assertions.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

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


@TestInstance(Lifecycle.PER_CLASS)
public class WhenPublishingAEventTest implements EventHandler<TestEvent> {

    private final TestEvent toBePublished;
    private final KafkaEventConsumer consumer;
    private TestEvent publishedEvent;

    @Override
    public void handle(TestEvent event) {
        System.out.println(String.format("Handling event Thread ID %s", Thread.currentThread().getId()));
        publishedEvent = event;
    }

    public WhenPublishingAEventTest() {
        KafkaConfig config = new KafkaConfig("localhost:9092", "cqrs-kafka-eventbus-test", 1);
        Deserializer deserializer = new JsonDeserializer();
        Serializer serializer = new JsonSerializer();
        ActionHandlerResolver resolver = new ActionHandlerResolver();
        resolver.registerActionHandler(this);
        toBePublished = new TestEvent(ID.fromObject(UUID.randomUUID()), "Awesomeness has happened", 1);

        consumer = new KafkaEventConsumer(config, serializer, deserializer, resolver);
        consumer.start("TestEvent");

        // First time topic consumer group creation takes bit of time in kafka (~3-4 seconds)
        await().atLeast(3, SECONDS).pollDelay(3, SECONDS).until(() -> true);

        EventPublisher publisher = new KafkaEventPublisher(config, serializer, deserializer);
        publisher.publish("TestEvent", toBePublished);

        await().atMost(6, SECONDS).until(() -> publishedEvent != null);
    }

    @Test
    public void testItShouldPublishTheEventToTheRegisteredEventHandler() {
        assertNotNull(publishedEvent, "The published event is not received in the registered handler");
        assertThat(publishedEvent).isNotNull()
        .hasStreamId(toBePublished.getStreamId())
        .hasEventId()
        .hasVersion(toBePublished.getVersion());
    assertEquals(toBePublished.getStuff(), publishedEvent.getStuff(), "The published event stuff value has changed");
    
    }

   /* @Test
    public void testItShouldHaveTheSameValuesForProperties() {
        assertThat(publishedEvent).isNotNull()
            .hasStreamId(toBePublished.getStreamId())
            .hasEventId()
            .hasVersion(toBePublished.getVersion());
        assertEquals(toBePublished.getStuff(), publishedEvent.getStuff(), "The published event stuff value has changed");
    } */

    @AfterAll
    private void tearDown() {
        try {
            consumer.close();
        } catch (Exception e) {

        }
    }
}
