package com.cqrs.example.taskmanager.commands;

import java.time.Instant;

import com.cqrs.messaging.Command;
import com.cqrs.messaging.ID;

public class CompleteTask extends Command {

    private static final long serialVersionUID = 1L;

    private final ID id;
    private final Instant completedAt;

    public CompleteTask(ID id, Instant completedAt) {
        this.id = id;
        this.completedAt = completedAt;
    }

    public ID getId() {
        return id;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
