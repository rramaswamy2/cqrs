package com.cqrs.eventbus;

import com.cqrs.messaging.Event;

public interface EventPublisher {
    <T extends Event> void publish(String streamName, T event);
    <T extends Event> void publish(String streamName, Iterable<T> events);
}
