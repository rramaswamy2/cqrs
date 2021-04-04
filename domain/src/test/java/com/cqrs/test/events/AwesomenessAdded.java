package com.cqrs.test.events;

import java.io.Serializable;
import java.util.UUID;

import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

public class AwesomenessAdded extends Event implements Serializable {
    public final String stuff;

    public AwesomenessAdded(ID id, String stuff) {
        super();
        this.setStreamId(id);
        this.setEventId(ID.fromObject(UUID.randomUUID()));
        this.stuff = stuff;
    }
}