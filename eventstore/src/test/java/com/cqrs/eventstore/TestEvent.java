package com.cqrs.eventstore;

import java.io.Serializable;
import java.util.UUID;

import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

public class TestEvent extends Event implements Serializable {
    public final String stuff;
    
    public TestEvent() {
        this(null, null, 0);
    }

    public TestEvent(ID id, String stuff, int version) {
        super();
        this.setStreamId(id);
        this.setEventId(ID.fromObject(UUID.randomUUID()));
        this.setVersion(version);
        this.stuff = stuff;
    }

}

