package com.cqrs.eventstore.sql;

import com.cqrs.eventstore.EventStoreException;

public class EventStoreMigrationException extends EventStoreException {

    public EventStoreMigrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
