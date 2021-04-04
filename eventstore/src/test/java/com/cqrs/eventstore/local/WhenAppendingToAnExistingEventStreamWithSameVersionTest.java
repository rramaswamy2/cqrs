package com.cqrs.eventstore.local;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cqrs.eventstore.ConcurrencyException;
import com.cqrs.eventstore.EventStore;
import com.cqrs.eventstore.TestEvent;
import com.cqrs.eventstore.local.InMemoryEventStore;
import com.cqrs.messaging.ID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WhenAppendingToAnExistingEventStreamWithSameVersionTest {

    private final EventStore store;
    private final ID eventStreamId;
    private final List<TestEvent> eventStream;
    private ConcurrencyException exception;

    public WhenAppendingToAnExistingEventStreamWithSameVersionTest() {
        store = new InMemoryEventStore();
        eventStreamId = ID.fromObject(UUID.randomUUID());
        List<TestEvent> originalEventStream = Arrays.asList(
                new TestEvent(eventStreamId, "Awesomeness has happened", 1),
                new TestEvent(eventStreamId, "more awesomeness has happened", 2)
        );
        store.save("my-test-stream", eventStreamId.toString(), originalEventStream, 0);
        eventStream = Arrays.asList(
                new TestEvent(eventStreamId, "Now things are really going crazy", 2),
                new TestEvent(eventStreamId, "Things must be going completely nuts now", 3)
        );
        try {
            store.save("my-test-stream", eventStreamId.toString(), eventStream, 1);
        } catch (ConcurrencyException e) {
            exception = e;
        }
    }

    @Test
    public void testItThrowsAConcurrencyException() {
        assertNotNull(exception, "ConcurrencyException not thrown");
    }

    @Test
    public void testItShouldMentionTheStreamId() {
        assertEquals(eventStreamId.toString(), exception.getStreamId(), "StreamId not matching");
    }

    @Test
    public void testItShouldMentionTheActualVersion() {
        assertEquals(2, exception.getActualVersion(), "Actual version not matching");
    }

    @Test
    public void testItShouldMentionTheExpectedVersion() {
        assertEquals(1, exception.getExpectedVersion(), "Expected version not matching");
    }
}
