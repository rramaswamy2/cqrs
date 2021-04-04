package com.cqrs.eventbus.serialize;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cqrs.eventbus.events.TestEvent;
import com.cqrs.eventbus.serialize.EventEnvelope;
import com.cqrs.messaging.Deserializer;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;
import com.cqrs.messaging.JsonDeserializer;
import com.cqrs.messaging.JsonSerializer;
import com.cqrs.messaging.Serializer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WhenDeserializingASerializedEvent {

    private final EventEnvelope originalTestEventEnvelope;
    private final EventEnvelope deserializedTestEventEnvelope;

    public WhenDeserializingASerializedEvent() {
        Serializer serializer = new JsonSerializer();
        Deserializer deserializer = new JsonDeserializer();
        Event testEvent = new TestEvent(ID.fromObject(UUID.randomUUID()), "Awesomeness", 0);
        this.originalTestEventEnvelope = EventEnvelope.fromEvent(testEvent, serializer, deserializer);
        String serializedData = serializer.serialize(this.originalTestEventEnvelope);
        this.deserializedTestEventEnvelope = deserializer.deserialize(serializedData, EventEnvelope.class);
    }

    @Test
    public void theDeserializedEventShouldNotBeNull() {
        assertNotNull(this.deserializedTestEventEnvelope);
    }

    @Test
    public void theDeserializedEventShouldBeIdenticalToTheOriginalEvent() {
        assertThat(this.deserializedTestEventEnvelope, samePropertyValuesAs(this.originalTestEventEnvelope));
    }
}
