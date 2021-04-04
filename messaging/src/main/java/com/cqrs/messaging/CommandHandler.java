package com.cqrs.messaging;

public interface CommandHandler <T extends Command> extends ActionHandler<T> {
    void handle(T command);
}
