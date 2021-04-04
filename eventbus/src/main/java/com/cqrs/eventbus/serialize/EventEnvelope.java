package com.cqrs.eventbus.serialize;

import java.time.Instant;
import java.util.Map;

import com.cqrs.messaging.Deserializer;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.Serializer;

public class EventEnvelope {
    public final String eventType;
    public final Map eventData;
    public final Instant timestamp;

    private EventEnvelope() {
        this(null, null, null);
    }

    public EventEnvelope(String eventType, Map eventData, Instant timestamp) {
        this.eventType = eventType;
        this.eventData = eventData;
        this.timestamp = timestamp;
    }

    public EventEnvelope(String eventType, Map eventData) {
        this(eventType, eventData, Instant.now());
    }

    public static EventEnvelope fromEvent(Event event, Serializer serializer, Deserializer deserializer) {
        String json = serializer.serialize(event);
        Map<String, Object> eventData = deserializer.deserialize(json, Map.class);
        return new EventEnvelope(event.getClass().getSimpleName(), eventData);
    }
}
