package com.cqrs.eventstore;

import java.util.List;

import com.cqrs.messaging.Event;

public interface EventStore {
    void save(String streamName, String streamId, Iterable<? extends Event> events, int expectedVersion);
    Iterable<? extends Event> getById(String streamId);
    List<String> getAllAggregateIds();
}
