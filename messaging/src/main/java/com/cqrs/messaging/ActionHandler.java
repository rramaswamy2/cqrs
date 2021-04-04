package com.cqrs.messaging;

public interface ActionHandler<T extends Action> {
    void handle(T action);
}
