package com.cqrs.example.taskmanager.domain;

import java.time.Instant;

import com.cqrs.domain.AggregateRoot;
import com.cqrs.example.taskmanager.events.TaskCompleted;
import com.cqrs.example.taskmanager.events.TaskCreated;
import com.cqrs.example.taskmanager.events.TaskDescriptionAdded;
import com.cqrs.example.taskmanager.events.TaskDueDateChanged;
import com.cqrs.example.taskmanager.events.TaskStatusChanged;
import com.cqrs.example.taskmanager.events.TaskTitleChanged;
import com.cqrs.messaging.ID;
import com.google.common.base.Strings;

public class Task extends AggregateRoot {

    private static final String DUE_DATE = "due date";

    private String title;
    private String description;
    private Instant creationDate;
    private Instant dueDate;
    private Instant completedAt;
    private String status;
    
    protected Task() {
    	
    }

    Task(ID id, String title, Instant dueDate) {
        super(id);

        if (Strings.isNullOrEmpty(title)) {
            throw new TaskException(id, "Title is mandatory");
        }

        if (dueDate == null) {
            dueDate = Instant.now();
        }
        isInFutureOrToday(dueDate, DUE_DATE);

        this.applyChange(new TaskCreated(id, title, Instant.now(), dueDate, this.getVersion() + 1));
    }

    void changeTitle(String title) {
        if (Strings.isNullOrEmpty(title)) {
            throw new TaskException(getId(), "Task title cannot be empty");
        } else if (this.title.equals(title)) {
            throw new TaskNothingChangedException(getId(), "title", this.title);
        }

        this.applyChange(new TaskTitleChanged(this.getId(), title, this.getVersion() + 1));
    }
    
    void changeStatus(String status) {
    	    	
    	if(Strings.isNullOrEmpty(status)) {
    		
    		throw new TaskException(getId(), "Task status cannot be empty");
    	}
    	
    	this.applyChange(new TaskStatusChanged(this.getId(), status, this.getVersion()+1) );
    }

    void changeDueDate(Instant dueDate) {
        if (dueDate == null) {
            throw new TaskException(getId(), "Task due date can't be null");
        } else if (this.dueDate != null && this.dueDate.equals(dueDate)) {
            throw new TaskNothingChangedException(getId(), DUE_DATE, this.dueDate.toString());
        }

        isInFutureOrToday(dueDate, DUE_DATE);

        isCompleted();

        this.applyChange(new TaskDueDateChanged(this.getId(), dueDate, this.getVersion() + 1));
    }

    void addDescription(String newDescription) {

        if (Strings.isNullOrEmpty(newDescription)) {
            throw new TaskException(getId(), "Task description cannot be empty");
        } else if (this.description != null && this.description.equalsIgnoreCase(newDescription)) {
            throw new TaskNothingChangedException(getId(), "description", this.description);
        }

        this.applyChange(new TaskDescriptionAdded(this.getId(), newDescription, this.getVersion() + 1));
    }
    
    

    void complete(Instant completedAt) {
        isCompleted();

        if (completedAt == null) {
            completedAt = Instant.now();
        } else if (completedAt.isBefore(this.creationDate)) {
            throw new TaskException(getId(), "Completion date must be after the creation date");
        }

        this.applyChange(new TaskCompleted(this.getId(), completedAt, this.getVersion() + 1));
    }

    private void isInFutureOrToday(Instant date, String detail) {
        if (date.isBefore(Instant.now())) {
            throw new TaskException(getId(), String.format("Task %s has to be in the future", detail));
        }
    }

    private void isCompleted() {
        if (this.completedAt != null) {
            throw new TaskException(getId(), String.format("This task is already completed at %s", this.completedAt));
        }
    }

    @SuppressWarnings("unused")
    private void handle(TaskCreated event) {
        setId(event.getStreamId());
        this.title = event.getTitle();
        this.creationDate = event.getCreationDate();
        this.dueDate = event.getDueDate();
    }

    @SuppressWarnings("unused")
    private void handle(TaskTitleChanged event) {
        this.title = event.getTitle();
    }

    @SuppressWarnings("unused")
    private void handle(TaskDueDateChanged event) {
        this.dueDate = event.getDueDate();
    }
    
    
    @SuppressWarnings("unused")
	private void handle(TaskStatusChanged event) {
    	
    	this.status = event.getStatus();
    }

    @SuppressWarnings("unused")
    private void handle(TaskDescriptionAdded event) {
        this.description = event.getDescription();
    }

    @SuppressWarnings("unused")
    private void handle(TaskCompleted event) {
        this.completedAt = event.getCompletedAt();
    }

    @Override
    public String toString() {
        return "Task [title=" + title + ", description=" + description + ", creationDate=" + creationDate + ", dueDate="
                + dueDate + ", completedAt=" + completedAt + ", getId()=" + getId() + ", getVersion()=" + getVersion()
                + ", getUncommittedChanges()=" + getUncommittedChanges() + "]";
    }

    public String getStatus() {
        return status;
    }
    
    

    
}
