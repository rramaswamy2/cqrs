package com.cqrs.eventbus.rabbitmq;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventbus.events.TestEvent;
import com.cqrs.eventbus.rabbitmq.RabbitMqConfig;
import com.cqrs.eventbus.rabbitmq.RabbitmqEventConsumer;
import com.cqrs.eventbus.rabbitmq.RabbitmqEventPublisher;
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

public class WhenPublishingAEventTest implements EventHandler<TestEvent> {

    private final TestEvent toBePublished;
    private TestEvent publishedEvent;

    @Override
    public void handle(TestEvent event) {
        publishedEvent = event;
    }

    public WhenPublishingAEventTest() {
        Deserializer deserializer = new JsonDeserializer();
        Serializer serializer = new JsonSerializer();

        ActionHandlerResolver resolver = new ActionHandlerResolver();
        resolver.registerActionHandler(this);

        RabbitMqConfig config = new RabbitMqConfig("localhost", 5672);
        RabbitmqEventConsumer consumer = new RabbitmqEventConsumer(config, deserializer, serializer, resolver);
        consumer.start("my-test-stream");

        toBePublished = new TestEvent(ID.fromObject(UUID.randomUUID()), "Awesomeness has happened", 1);
        EventPublisher publisher = new RabbitmqEventPublisher(config, serializer, deserializer);
        publisher.publish("my-test-stream", toBePublished);
        await().atMost(5, SECONDS).until(() -> publishedEvent != null);
    }

    @Test
    public void testItShouldPublishTheEventToTheRegisteredEventHandler() {
        assertNotNull(publishedEvent, "The published event is not received in the registered handler");
    }

    @Test
    public void testItShouldHaveTheSameValuesForProperties() {
        assertThat(publishedEvent).isNotNull()
            .hasStreamId(toBePublished.getStreamId())
            .hasEventId()
            .hasVersion(toBePublished.getVersion());

        assertEquals(toBePublished.getStuff(), publishedEvent.getStuff(), "The published event stuff value has changed");
    }
}
