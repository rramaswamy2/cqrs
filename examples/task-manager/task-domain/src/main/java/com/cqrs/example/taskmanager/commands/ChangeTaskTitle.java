package com.cqrs.example.taskmanager.commands;

import com.cqrs.messaging.Command;
import com.cqrs.messaging.ID;

public class ChangeTaskTitle extends Command {

    private static final long serialVersionUID = 1L;

    private final ID id;
    private final String title;

    public ChangeTaskTitle(ID id, String title) {
        this.id = id;
        this.title = title;
    }

    public ID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
