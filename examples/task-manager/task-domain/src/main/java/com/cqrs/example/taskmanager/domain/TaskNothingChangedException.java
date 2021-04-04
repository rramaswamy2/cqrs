package com.cqrs.example.taskmanager.domain;

import com.cqrs.messaging.ID;

public class TaskNothingChangedException extends TaskException {

    public TaskNothingChangedException(ID aggregateId, String field, String currentValue) {
        super(aggregateId, String.format("Task %s didn't change compared to current value %s", field, currentValue));
    }
}
