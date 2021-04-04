package com.cqrs.example.taskmanager.commands;

import java.util.Objects;

import com.cqrs.messaging.Command;
import com.cqrs.messaging.ID;

public class AddDescriptionToTask extends Command {

    private static final long serialVersionUID = 1L;

    private ID id;
    private String description;
    
    public AddDescriptionToTask() { 
    	
    }

    public AddDescriptionToTask(ID id, String description) {
        this.id = id;
        this.description = description;
    }

    public ID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

	
	@Override
	public String toString() {
		return "AddDescriptionToTask [id=" + id + ", description=" + description + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(description, id);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AddDescriptionToTask other = (AddDescriptionToTask) obj;
		return Objects.equals(description, other.description) && Objects.equals(id, other.id);
	}

	
	
	
    
    
}