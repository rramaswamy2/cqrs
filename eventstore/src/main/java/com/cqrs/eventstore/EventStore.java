package com.cqrs.eventstore;

import com.cqrs.messaging.Event;

public interface EventStore {
    void save(String streamName, String streamId, Iterable<? extends Event> events, int expectedVersion);
    Iterable<? extends Event> getById(String streamId);
}
