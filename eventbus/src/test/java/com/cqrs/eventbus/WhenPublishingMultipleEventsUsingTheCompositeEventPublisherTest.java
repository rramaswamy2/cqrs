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

public class WhenPublishingMultipleEventsUsingTheCompositeEventPublisherTest implements EventHandler<TestEvent> {

    private final ArrayList<TestEvent> toBePublished;
    private ArrayList<TestEvent> publishedEvents = new ArrayList<>();
    private int count;

    @Override
    public synchronized void handle(TestEvent event) {
        if (!publishedEvents.stream().anyMatch(e -> e.getVersion() == event.getVersion())) {
            publishedEvents.add(event);
        }
        count += 1;
    }

    public WhenPublishingMultipleEventsUsingTheCompositeEventPublisherTest() {
        ActionHandlerResolver resolver = new ActionHandlerResolver();
        resolver.registerActionHandler(this);

        EventPublisher publisher = new CompositePublisher(new ArrayList<EventPublisher>() {{
            add(new SimpleEventPublisher(resolver));
            add(new SimpleEventPublisher(resolver));
        }});
        ID id = ID.fromObject(UUID.randomUUID());
        toBePublished = new ArrayList<TestEvent>() {{
            add(new TestEvent(id, "Awesomeness has happened", 1));
            add(new TestEvent(id, "Awesomeness has happened twice", 2));
        }};
        publisher.publish("my-test-stream", toBePublished);
    }

    @Test
    public void testItShouldPublishTheEventToTheRegisteredCommandHandlerFourTimes() {
        long uniqueEventCounts = publishedEvents.stream().filter(e -> e.getVersion() == 1 || e.getVersion() == 2).count();
        assertEquals(2, uniqueEventCounts, "The published event is not received in the registered handler");
        assertEquals(4, count, "The events have not been published by all the publishers");
    }

    @Test
    public void testItShouldHaveTheSameValuesForPropertiesInFirstEvent() {
        assertThat(publishedEvents.get(0)).isNotNull()
            .hasStreamId(toBePublished.get(0).getStreamId())
            .hasEventId()
            .hasVersion(toBePublished.get(0).getVersion());
        assertEquals(toBePublished.get(0).getStuff(), publishedEvents.get(0).getStuff(), "The published event stuff value has changed");
    }

    @Test
    public void testItShouldHaveTheSameValuesForPropertiesInSecondEvent() {
        assertThat(publishedEvents.get(1)).isNotNull()
            .hasStreamId(toBePublished.get(1).getStreamId())
            .hasEventId()
            .hasVersion(toBePublished.get(1).getVersion());

        assertEquals(toBePublished.get(1).getStuff(), publishedEvents.get(1).getStuff(), "The published event stuff value has changed");
    }
}
