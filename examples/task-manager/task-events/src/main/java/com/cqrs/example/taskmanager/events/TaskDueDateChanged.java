package com.cqrs.example.taskmanager.events;

import java.time.Instant;
import java.util.UUID;

import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

public class TaskDueDateChanged extends Event {

    private static final long serialVersionUID = 1L;

    private final Instant dueDate;

    private TaskDueDateChanged() {
        this(null, null, 0);
    }

    public TaskDueDateChanged(ID id, Instant dueDate, int version) {
        super();
        this.setStreamId(id);
        this.setEventId(ID.fromObject(UUID.randomUUID()));
        this.setVersion(version);
        this.dueDate = dueDate;
    }

    public Instant getDueDate() {
        return dueDate;
    }
}
