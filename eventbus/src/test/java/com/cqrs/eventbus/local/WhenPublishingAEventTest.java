package com.cqrs.eventbus.local;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventbus.events.TestEvent;
import com.cqrs.eventbus.local.SimpleEventPublisher;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.EventHandler;
import com.cqrs.messaging.ID;

import static com.cqrs.assertj.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhenPublishingAEventTest implements EventHandler<TestEvent> {

    private final EventPublisher publisher;
    private final TestEvent toBePublished;
    private TestEvent publishedEvent;

    @Override
    public void handle(TestEvent event) {
        publishedEvent = event;
    }

    public WhenPublishingAEventTest() {
        ActionHandlerResolver resolver = new ActionHandlerResolver();
        resolver.registerActionHandler(this);
        publisher = new SimpleEventPublisher(resolver);
        toBePublished = new TestEvent(ID.fromObject(UUID.randomUUID()), "Awesomeness has happened", 1);
        publisher.publish("my-test-stream", toBePublished);
    }

    @Test
    public void testItShouldPublishTheEventToTheRegisteredEventHandler() {
        assertEquals(toBePublished, publishedEvent, "The published event is not received in the registered handler");
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
