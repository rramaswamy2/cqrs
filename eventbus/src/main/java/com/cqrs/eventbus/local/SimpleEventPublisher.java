package com.cqrs.eventbus.local;

import java.util.List;
import java.util.Objects;

import com.cqrs.eventbus.EventPublisher;
import com.cqrs.messaging.ActionHandler;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.Event;

public class SimpleEventPublisher implements EventPublisher {

    private final ActionHandlerResolver resolverProvider;

    public SimpleEventPublisher(ActionHandlerResolver resolverProvider) {
        this.resolverProvider = resolverProvider;
    }

    @Override
    public <T extends Event> void publish(String streamName, T event) {
        List<ActionHandler> handlers = resolverProvider.findHandlersFor(event.getClass().getSimpleName());

        handlers.stream().filter(Objects::nonNull).forEach(handler -> handler.handle(event));
    }

    public <T extends Event> void publish(String streamName, Iterable<T> events) {
        events.forEach(event -> publish(streamName, event));
    }
}
