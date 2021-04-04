package com.cqrs.eventstore.sql;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

import com.cqrs.eventstore.TestEvent;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhenAppendingToAnExistingEventStreamTest extends SqlEventStoreTest {

    private final ID eventStreamId;
    private final ID eventIdOne;
    private final ID eventIdTwo;

    private final List<TestEvent> eventStream;

    public WhenAppendingToAnExistingEventStreamTest() {
        String streamName = WhenAppendingToAnExistingEventStreamTest.class.getCanonicalName();
        eventStreamId = ID.fromObject(UUID.randomUUID());
        eventIdOne = ID.fromObject(UUID.randomUUID());
        eventIdTwo = ID.fromObject(UUID.randomUUID());
        List<Event> originalEventStream = Arrays.asList(new TestEvent(eventIdOne, "Awesomeness has happened", 1));
        storeEvents(eventStreamId.toString(), originalEventStream, streamName);
        eventStream = Arrays.asList(
                new TestEvent(eventIdTwo, "Now things are really going crazy", 2)
        );
        store.save(streamName, eventStreamId.toString(), eventStream, 1);
    }

    @Test
    public void testTheEventStoreShouldHaveTwoEventsInTheStream() {
        Iterable<? extends Event> storedEvents = store.getById(eventStreamId.toString());
        long storedEventCount = StreamSupport.stream(storedEvents.spliterator(), false).count();
        assertEquals(eventStream.size() + 1, storedEventCount, "The stream size differs");
    }

    @Test
    public void testTheThirdtEventShouldHaveVersionThree() {
        Iterable<? extends Event> storedEvents = store.getById(eventStreamId.toString());
        assertEquals(2, toList(storedEvents).get(1).getVersion(), "The third event version has a mismatch");
    }

    @Test
    public void testTheFourthEventShouldHaveVersionFour() {
        Iterable<? extends Event> storedEvents = store.getById(eventStreamId.toString());
        assertEquals(2, toList(storedEvents).get(1).getVersion(), "The fourth event version has a mismatch");
    }
}
