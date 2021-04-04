package com.cqrs.eventstore.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cqrs.eventstore.ConcurrencyException;
import com.cqrs.eventstore.TestEvent;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WhenAppendingToAnExistingEventStreamWithSameVersionTest extends SqlEventstoreExceptionTest<ConcurrencyException> {

    private final ID eventStreamId;

    private List<TestEvent> eventStream = new ArrayList<>();

    public WhenAppendingToAnExistingEventStreamWithSameVersionTest() {
        String streamName = WhenAppendingToAnExistingEventStreamWithSameVersionTest.class.getCanonicalName();

        eventStreamId = ID.fromObject(UUID.randomUUID());
        ID eventIdOne = ID.fromObject(UUID.randomUUID());
        ID eventIdTwo = ID.fromObject(UUID.randomUUID());
        ID eventIdThree = ID.fromObject(UUID.randomUUID());
        List<Event> originalEventStream = Arrays.asList(
                new TestEvent(eventIdOne, "Awesomeness has happened", 1),
                new TestEvent(eventIdTwo, "Crazyness", 2)
        );
        storeEvents(eventStreamId.toString(), originalEventStream, streamName);
        try {
            eventStream = Arrays.asList(
                    new TestEvent(eventIdThree, "Now things are really going crazy", 2)
            );
            store.save(streamName, eventStreamId.toString(), eventStream, 2);
        } catch (ConcurrencyException e) {
            exception = e;
        }
    }

    @Test
    public void testItShouldThrowAConcurrencyException() {
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
        assertEquals(2, exception.getExpectedVersion(), "Expected version not matching");
    }
}
