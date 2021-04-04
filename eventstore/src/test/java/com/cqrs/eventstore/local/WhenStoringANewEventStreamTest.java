package com.cqrs.eventstore.local;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

import com.cqrs.eventstore.EventStore;
import com.cqrs.eventstore.TestEvent;
import com.cqrs.eventstore.local.InMemoryEventStore;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhenStoringANewEventStreamTest {

    private final EventStore store;
    private final ID eventStreamId;
    private final List<TestEvent> eventStream;

    public WhenStoringANewEventStreamTest() {
        store = new InMemoryEventStore();
        eventStreamId = ID.fromObject(UUID.randomUUID());
        eventStream = Arrays.asList(
                new TestEvent(eventStreamId, "Awesomeness has happened", 1),
                new TestEvent(eventStreamId, "more awesomeness has happened", 2)
        );
        store.save("my-test-stream", eventStreamId.toString(), eventStream, 0);
    }

    @Test
    public void testTheEventStoreShouldHaveTwoEventsInTheStream() {
        Iterable<? extends Event> storedEvents = store.getById(eventStreamId.toString());
        long storedEventCount = StreamSupport.stream(storedEvents.spliterator(), false).count();
        assertEquals(eventStream.size(), storedEventCount, "The stream size differs");
    }

    @Test
    public void testTheFirstEventShouldHaveVersionOne() {
        Iterable<? extends Event> storedEvents = store.getById(eventStreamId.toString());
        assertEquals(1, toList(storedEvents).get(0).getVersion(), "The first event version has a mismatch");
    }

    @Test
    public void testTheSecondEventShouldHaveVersionTwo() {
        Iterable<? extends Event> storedEvents = store.getById(eventStreamId.toString());
        assertEquals(2, toList(storedEvents).get(1).getVersion(), "The second event version has a mismatch");
    }

    private static <T> List<T> toList(Iterable<T> iterable) {
        if (iterable instanceof List) {
            return (List<T>) iterable;
        }

        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }
}
