package com.cqrs.eventbus.events;

import java.util.UUID;

import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

public class TestEvent extends Event {
    private final String stuff;

    public TestEvent() {
        this(null, null, 0);
    }

    public TestEvent(ID id, String stuff, int version) {
        this.setStreamId(id);
        this.setEventId(ID.fromObject(UUID.randomUUID()));
        this.setVersion(version);
        this.stuff = stuff;
    }

    public String getStuff() {
        return this.stuff;
    }

	@Override
	public String toString() {
		return "TestEvent [stuff=" + stuff + ", getStreamId()=" + getStreamId() + ", getEventId()=" + getEventId()
				+ ", getVersion()=" + getVersion() + "]";
	}
    
    
}
