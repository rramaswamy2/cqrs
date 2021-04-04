package com.cqrs.eventbus.serialize;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cqrs.eventbus.events.TestEvent;
import com.cqrs.eventbus.serialize.EventEnvelope;
import com.cqrs.messaging.Deserializer;
import com.cqrs.messaging.ID;
import com.cqrs.messaging.JsonDeserializer;
import com.cqrs.messaging.JsonSerializer;
import com.cqrs.messaging.Serializer;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WhenSerializingAnEvent {

    private final String serializedData;
    private final TestEvent testEvent;

    public WhenSerializingAnEvent() {
        Serializer serializer = new JsonSerializer();
        Deserializer deserializer = new JsonDeserializer();
        testEvent = new TestEvent(ID.fromObject(UUID.randomUUID()), "Awesomeness", 0);
        serializedData = serializer.serialize(EventEnvelope.fromEvent(this.testEvent, serializer, deserializer));
    }

    @Test
    public void itShouldBeWrappedInAnEnvelope() {
        assertTrue(serializedData.contains("eventType"));
        assertTrue(serializedData.contains("eventData"));
        assertTrue(serializedData.contains("timestamp"));
    }

    @Test
    public void itShouldHaveAnEventWithVersion0() {
        assertTrue(serializedData.contains("\"version\":0"));
    }

    @Test
    public void itShouldHaveAnEventWithTheEventId() {
        assertTrue(serializedData.contains("\"eventId\":\"" + testEvent.getEventId() + "\""));
    }

    @Test
    public void itShouldHaveAnEventWithTheStreamId() {
        assertTrue(serializedData.contains("\"streamId\":\"" + testEvent.getStreamId() + "\""));
    }

    @Test
    public void itShouldHaveAnEventWithValueAwesomenessForAttributeStuff() {
        assertTrue(serializedData.contains("\"stuff\":\"Awesomeness\""));
    }

    @Test
    public void itShouldHaveATimestampInISO8601format() {
        assertTrue(serializedData.matches(".*\"timestamp\":\"(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2})\\:(\\d{2})\\:(\\d{2})\\.(.*?)"));
    	
    	
    }
}
