package com.cqrs.eventstore.sql;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Iterables;
import org.junit.jupiter.api.Test;

import com.cqrs.eventstore.TestEvent;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class WhenGettingAEventStreamTest extends SqlEventStoreTest {
    private ID streamId;
    private Iterable<? extends Event> storedEvents;

    public WhenGettingAEventStreamTest() {
        List<Event> events = Arrays.asList(
                new TestEvent(ID.fromObject(UUID.randomUUID()), "Awesomeness has happened one", 1),
                new TestEvent(ID.fromObject(UUID.randomUUID()), "Awesomeness has happened two", 2)
        );
        streamId = ID.fromObject(UUID.randomUUID());
        storeEvents(streamId.toString(), events, WhenGettingAEventStreamTest.class.getCanonicalName());
        storedEvents = store.getById(streamId.toString());

    }

    @Test
    public void testStreamIsNotEmpty() {
        assertFalse(Iterables.isEmpty(storedEvents), "Stream is empty");
    }

    @Test
    public void testStreamHasTwoEvents() {
        assertEquals(2, Iterables.size(storedEvents), "Stream is empty");
    }

}

