package com.cqrs.eventstore.sql;

import java.util.UUID;

import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;
import com.google.common.collect.Iterables;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WhenGettingNonExistingStreamTest extends SqlEventStoreTest {
    private final Iterable<? extends Event> events;

    public WhenGettingNonExistingStreamTest() {
        events = store.getById(ID.fromObject(UUID.randomUUID()).toString());
    }

    @Test
    public void testStreamNotFound() {
        assertTrue(Iterables.isEmpty(events), "Stream is exist");
    }

}

