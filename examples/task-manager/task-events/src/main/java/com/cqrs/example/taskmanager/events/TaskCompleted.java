package com.cqrs.example.taskmanager.events;

import java.time.Instant;
import java.util.UUID;

import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

public class TaskCompleted extends Event {

    private static final long serialVersionUID = 1L;

    private final Instant completedAt;

    private TaskCompleted() {
        this(null, Instant.now(), 0);
    }

    public TaskCompleted(ID id, Instant completedAt, int version) {
        super();
        this.setStreamId(id);
        this.setEventId(ID.fromObject(UUID.randomUUID()));
        this.setVersion(version);
        this.completedAt = completedAt;
    }

    public Instant getCompletedAt() {
        return this.completedAt;
    }
}
