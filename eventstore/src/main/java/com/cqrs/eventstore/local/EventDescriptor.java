package com.cqrs.eventstore.local;

import com.cqrs.messaging.Event;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class EventDescriptor implements Serializable {

    private static final long serialVersionUID = 1L;

    public final String streamId;
    public final Event event;
    public final int version;

    @JsonCreator
    public EventDescriptor(
            @JsonProperty("streamId") String streamId,
            @JsonProperty("event") Event event,
            @JsonProperty("version") int version) {
        this.streamId = streamId;
        this.event = event;
        this.version = version;
    }
}
