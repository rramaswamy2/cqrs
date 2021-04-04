package com.cqrs.assertj;

import java.util.UUID;

import org.assertj.core.api.AbstractAssert;

import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

public class EventAssert extends AbstractAssert<EventAssert, Event> {

    EventAssert(Event actual) {
        super(actual, EventAssert.class);
    }

    public EventAssert hasStreamId(ID id) {
        isNotNull();
        if (!actual.getStreamId().equals(id)) {
            failWithMessage("Expected event to have stream id %s but was %s", id, actual.getStreamId());
        }
        return this;
    }

    public EventAssert hasEventId() {
        isNotNull();
        if (actual.getEventId() == null ||
            actual.getEventId() == ID.fromObject(new UUID(0L, 0L)) ||
            actual.getEventId() == ID.fromObject("")) {
            failWithMessage("Expected event to have event id but was %s", actual.getEventId());
        }
        return this;
    }

    public EventAssert hasEventId(ID id) {
        isNotNull();
        if (!actual.getEventId().equals(id)) {
            failWithMessage("Expected event to have event id %s but was %s", id, actual.getEventId());
        }
        return this;
    }

    public EventAssert hasVersion(int version) {
        isNotNull();
        if (actual.getVersion() != version) {
            failWithMessage("Expected event to have version %s but was %s", version, actual.getVersion());
        }
        return this;
    }
}
