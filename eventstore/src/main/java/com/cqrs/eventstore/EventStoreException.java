package com.cqrs.eventstore;

public class EventStoreException extends RuntimeException {

    public EventStoreException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public EventStoreException(Throwable throwable) {
        super(throwable);
    }
}
