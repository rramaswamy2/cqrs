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

public class WhenAppendingToAnExistingEventStreamTest {

    private final EventStore store;
    private final ID eventStreamId;
    private final List<TestEvent> eventStream;

    public WhenAppendingToAnExistingEventStreamTest() {
        store = new InMemoryEventStore();
        eventStreamId = ID.fromObject(UUID.randomUUID());
        List<TestEvent> originalEventStream = Arrays.asList(
                new TestEvent(eventStreamId, "Awesomeness has happened", 1),
                new TestEvent(eventStreamId, "more awesomeness has happened", 2)
        );
        store.save("my-test-stream", eventStreamId.toString(), originalEventStream, 0);
        eventStream = Arrays.asList(
                new TestEvent(eventStreamId, "Now things are really going crazy", 3),
                new TestEvent(eventStreamId, "Things must be going completely nuts now", 4)
        );
        store.save("my-test-stream", eventStreamId.toString(), eventStream, 2);
    }

    @Test
    public void testTheEventStoreShouldHaveTwoEventsInTheStream() {
        Iterable<? extends Event> storedEvents = store.getById(eventStreamId.toString());
        long storedEventCount = StreamSupport.stream(storedEvents.spliterator(), false).count();
        assertEquals(eventStream.size() + 2, storedEventCount, "The stream size differs");
    }

    @Test
    public void testTheThirdtEventShouldHaveVersionThree() {
        Iterable<? extends Event> storedEvents = store.getById(eventStreamId.toString());
        assertEquals(3, toList(storedEvents).get(2).getVersion(), "The third event version has a mismatch");
    }

    @Test
    public void testTheFourthEventShouldHaveVersionFour() {
        Iterable<? extends Event> storedEvents = store.getById(eventStreamId.toString());
        assertEquals(4, toList(storedEvents).get(3).getVersion(), "The fourth event version has a mismatch");
    }

    private static <T> List<T> toList(Iterable<T> iterable) {
        if (iterable instanceof List) {
            return (List<T>) iterable;
        }

        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }
}
