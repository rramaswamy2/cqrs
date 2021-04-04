package com.cqrs.example.taskmanager.domain;

import com.cqrs.domain.DomainException;
import com.cqrs.messaging.ID;

public class TaskException extends DomainException {

    public TaskException(ID aggregateId, String message) {
        super(aggregateId, message);
    }
}
