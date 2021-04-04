package com.cqrs.commandbus.commands;

import java.util.UUID;

import com.cqrs.messaging.Command;

public class TestCommand extends Command {
    private final UUID id;
    private final String stuff;
    
    public TestCommand() {
    	this(UUID.randomUUID(), null);
    }

    public TestCommand(UUID id, String stuff) {
        this.id = id;
        this.stuff = stuff;
    }

    public UUID getId() {
        return id;
    }

    public String getStuff() {
        return this.stuff;
    }

	@Override
	public String toString() {
		return "TestCommand [id=" + id + ", stuff=" + stuff + "]";
	}
    
    
    
    
}