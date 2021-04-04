package com.cqrs.example.taskmanager.commands;

import java.time.Instant;

import com.cqrs.messaging.Command;
import com.cqrs.messaging.ID;

public class ChangeTaskDueDate extends Command {

    private static final long serialVersionUID = 1L;

    private final ID id;
    private final Instant dueDate;

    public ChangeTaskDueDate(ID id, Instant dueDate) {
        this.id = id;
        this.dueDate = dueDate;
    }

    public ID getId() {
        return id;
    }

    public Instant getDueDate() {
        return dueDate;
    }
}
