package com.cqrs.example.taskmanager.commands;

import com.cqrs.messaging.Command;
import com.cqrs.messaging.ID;

public class ChangeTaskStatus extends Command {

    private static final long serialVersionUID = 1L;

    private  ID id;
    private  String status;
    
    public ChangeTaskStatus() {
    	
    }

    public ChangeTaskStatus(ID id, String status) {
        this.id = id;
        this.status = status;
    }

    public ID getId() {
        return id;
    }

	public String getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "ChangeTaskStatus [id=" + id + ", status=" + status + ", getId()=" + getId() + ", getStatus()="
				+ getStatus() + "]";
	}

	
	

   
}