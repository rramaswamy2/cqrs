package com.cqrs.example.taskmanager.commands;

import java.time.Instant;

import com.cqrs.messaging.Command;
import com.cqrs.messaging.ID;

public class CreateTask extends Command {

    private static final long serialVersionUID = 1L;

    private ID id;
    private String title;
    private Instant dueDate;
    
    public CreateTask() {
    	
    }

    public CreateTask(ID id, String title, Instant dueDate) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
    }

    public ID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Instant getDueDate() {
        return dueDate;
    }

}
