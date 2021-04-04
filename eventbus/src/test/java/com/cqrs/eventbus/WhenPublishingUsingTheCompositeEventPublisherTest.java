package com.cqrs.eventbus;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cqrs.eventbus.CompositePublisher;
import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventbus.events.TestEvent;
import com.cqrs.eventbus.local.SimpleEventPublisher;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.EventHandler;
import com.cqrs.messaging.ID;

import static com.cqrs.assertj.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhenPublishingUsingTheCompositeEventPublisherTest implements EventHandler<TestEvent> {

    private final TestEvent toBePublished;
    private TestEvent publishedEvent;
    private int count;

    @Override
    public synchronized void handle(TestEvent event) {
        publishedEvent = event;
        count += 1;
    }

    public WhenPublishingUsingTheCompositeEventPublisherTest() {
        ActionHandlerResolver resolver = new ActionHandlerResolver();
        resolver.registerActionHandler(this);

        EventPublisher publisher = new CompositePublisher(new ArrayList<EventPublisher>() {{
            add(new SimpleEventPublisher(resolver));
            add(new SimpleEventPublisher(resolver));
        }});
        toBePublished = new TestEvent(ID.fromObject(UUID.randomUUID()), "Awesomeness has happened", 1);
        publisher.publish("my-test-stream", toBePublished);
    }

    @Test
    public void testItShouldPublishTheEventToTheRegisteredCommandHandlerTwice() {
        assertEquals(toBePublished, publishedEvent, "The published event is not received in the registered handler");
        assertEquals(2, count, "The event has not been published by all the publishers");
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
