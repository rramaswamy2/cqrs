package com.cqrs.eventbus.serialize;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cqrs.eventbus.events.TestEvent;
import com.cqrs.messaging.Deserializer;
import com.cqrs.messaging.ID;
import com.cqrs.messaging.JsonDeserializer;
import com.cqrs.messaging.JsonSerializer;
import com.cqrs.messaging.Serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhenDeserializingAnEvent {
    private final TestEvent testEvent;
    private final TestEvent deserializedEvent;

    public WhenDeserializingAnEvent() {
        Serializer serializer = new JsonSerializer();
        Deserializer deserializer = new JsonDeserializer();
        testEvent = new TestEvent(ID.fromObject(UUID.randomUUID()), "Awesomeness", 0);
        String serializedData = serializer.serialize(testEvent);
        deserializedEvent = deserializer.deserialize(serializedData, TestEvent.class);
    }

    @Test
    public void itShouldHaveTheSameStreamId() {
        assertEquals(testEvent.getStreamId(), deserializedEvent.getStreamId(), "The streamId does not equal with the original");
    }

    @Test
    public void itShouldHaveTheSameEventId() {
        assertEquals(testEvent.getEventId(), deserializedEvent.getEventId(), "The eventId does not equal with the original");
    }

    @Test
    public void itShouldHaveTheSameStuff() {
        assertEquals(testEvent.getStuff(), deserializedEvent.getStuff(), "The stuff does not equal with the original");
    }

    @Test
    public void itShouldHaveTheSameVersion() {
        assertEquals(testEvent.getVersion(), deserializedEvent.getVersion(), "The version does not equal with the original");
    }
}
