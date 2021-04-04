package com.cqrs.example.taskmanager.events;

import java.util.UUID;

import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

public class TaskTitleChanged extends Event {

    private static final long serialVersionUID = 1L;

    private final String title;

    private TaskTitleChanged() {
        this(null, null, 0);
    }

    public TaskTitleChanged(ID id, String title, int version) {
        super();
        this.setStreamId(id);
        this.setEventId(ID.fromObject(UUID.randomUUID()));
        this.setVersion(version);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
