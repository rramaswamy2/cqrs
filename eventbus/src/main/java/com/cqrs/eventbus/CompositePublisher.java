package com.cqrs.eventbus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.cqrs.messaging.Event;

/**
 * This composite publisher can be used for invoking multiple publishers in one go.
 * <p>
 * E.g. First invoke the synchronous in memory publisher for local caching and then invoke one of the other
 * publishers which have some eventual consistency because of the asynchronous behavior and network boundary.
 */
public final class CompositePublisher implements EventPublisher {

    private final Supplier<Stream<EventPublisher>> publishers;

    public CompositePublisher(List<EventPublisher> publishers) {
        this.publishers = new ArrayList<>(publishers)::parallelStream;
    }

    @Override
    public <T extends Event> void publish(String streamName, T event) {
        publishers.get().forEach(p -> p.publish(streamName, event));
    }

    @Override
    public <T extends Event> void publish(String streamName, Iterable<T> events) {
        publishers.get().forEach(p -> p.publish(streamName, events));
    }
}
