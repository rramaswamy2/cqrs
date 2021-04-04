package com.cqrs.example.taskmanager.events;

import java.util.UUID;

import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

public class TaskStatusChanged extends Event {

    private static final long serialVersionUID = 1L;

    private final String status;

    @SuppressWarnings("unused")
	private TaskStatusChanged() {
        this(null, null, 0);
    }

    public TaskStatusChanged(ID id, String status, int version) {
        super();
        this.setStreamId(id);
        this.setEventId(ID.fromObject(UUID.randomUUID()));
        this.setVersion(version);
        this.status = status;
    }

	public String getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "TaskStatusChanged [status=" + status + ", getStreamId()=" + getStreamId() + ", getEventId()="
				+ getEventId() + ", getVersion()=" + getVersion() + "]";
	}
	
	
	
	

    
}