package com.cqrs.domain;

import com.cqrs.messaging.ID;

public class AggregateNotFoundException extends RuntimeException {
    public AggregateNotFoundException(String name, ID id) {
        super(name + "; Id=" + id.toString());
    }
}
