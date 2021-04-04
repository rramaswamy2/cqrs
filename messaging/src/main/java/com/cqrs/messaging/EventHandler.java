package com.cqrs.messaging;

public interface EventHandler<T extends Event> extends ActionHandler<T> {
    void handle(T event);
}
