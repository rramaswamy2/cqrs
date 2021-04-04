package com.cqrs.domain;

import java.util.UUID;

import com.cqrs.messaging.ID;

public class DomainException extends RuntimeException {

    private static final UUID EMPTY_UUID = new UUID(0L, 0L);

    private final ID aggregateId;

    public DomainException(String message) {
        this(ID.fromObject(EMPTY_UUID), message);
    }

    public DomainException(Throwable throwable) {
        this(ID.fromObject(EMPTY_UUID), throwable);
    }

    public DomainException(ID aggregateId, String message) {
        super(message);
        this.aggregateId = aggregateId;
    }

    public DomainException(ID aggregateId, Throwable throwable) {
        super(throwable);
        this.aggregateId = aggregateId;
    }

    public ID getAggregateId() {
        return aggregateId;
    }
}
