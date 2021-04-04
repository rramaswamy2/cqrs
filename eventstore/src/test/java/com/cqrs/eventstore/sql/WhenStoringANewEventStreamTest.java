package com.cqrs.eventstore.sql;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.assertj.db.type.Table;
import org.junit.jupiter.api.Test;

import com.cqrs.eventstore.TestEvent;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

import static org.assertj.db.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhenStoringANewEventStreamTest extends SqlEventStoreTest {

    private final ID eventStreamId;
    private final ID eventIDOne;
    private final ID eventIDTwo;
    private final List<TestEvent> eventStream;

    public WhenStoringANewEventStreamTest() {
        eventStreamId = ID.fromObject(UUID.randomUUID());
        eventIDOne = ID.fromObject(UUID.randomUUID());
        eventIDTwo = ID.fromObject(UUID.randomUUID());
        eventStream = Arrays.asList(
                new TestEvent(eventIDOne, "Awesomeness has happened", 1),
                new TestEvent(eventIDTwo, "more awesomeness has happened", 2)
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

    @Test
    public void testTheStreamVersionShouldEqualToTwo() {
        Table table = new Table(dataSource, "streams");
        assertThat(table).row(0).column("VERSION").value().equals(2);
    } 

}
