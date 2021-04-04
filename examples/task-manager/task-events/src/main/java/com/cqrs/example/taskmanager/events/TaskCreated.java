package com.cqrs.example.taskmanager.events;

import java.time.Instant;
import java.util.UUID;

import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

public class TaskCreated extends Event {

    private static final long serialVersionUID = 1L;

    private final String title;
    private final Instant creationDate;
    private final Instant dueDate;

    private TaskCreated() {
        this(null, null, null, null, 0);
    }

    public TaskCreated(ID id, String title, Instant creationDate, Instant dueDate, int version) {
        super();
        this.setStreamId(id);
        this.setEventId(ID.fromObject(UUID.randomUUID()));
        this.setVersion(version);
        this.title = title;
        this.creationDate = creationDate;
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public Instant getDueDate() {
        return dueDate;
    }

	@Override
	public String toString() {
		return "TaskCreated [title=" + title + ", creationDate=" + creationDate + ", dueDate=" + dueDate
				+ ", getStreamId()=" + getStreamId() + ", getEventId()=" + getEventId() + ", getVersion()="
				+ getVersion() + "]";
	}
    
    
}
