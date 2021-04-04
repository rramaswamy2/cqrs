package com.cqrs.example.taskmanager.events;

import java.util.UUID;

import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

public class TaskDescriptionAdded extends Event {

    private static final long serialVersionUID = 1L;

    private final String description;

    private TaskDescriptionAdded() {
        this(null, null, 0);
    }

    public TaskDescriptionAdded(ID id, String description, int version) {
        this.setStreamId(id);
        this.setEventId(ID.fromObject(UUID.randomUUID()));
        this.setVersion(version);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

	@Override
	public String toString() {
		return "TaskDescriptionAdded [description=" + description + ", getStreamId()=" + getStreamId()
				+ ", getEventId()=" + getEventId() + ", getVersion()=" + getVersion() + "]";
	}

	
    
    
}
