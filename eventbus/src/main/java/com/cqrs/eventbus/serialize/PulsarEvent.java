package com.cqrs.eventbus.serialize;

import java.io.Serializable;

import com.cqrs.messaging.Event;

public abstract class PulsarEvent extends Event implements Serializable {
    private String timestamp; 
    private String eventType;
    private String eventClassName; 
    private String aggregateType;
	
	private static final long serialVersionUID = 1L;

	public PulsarEvent() {
		super();
	}
	
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getAggregateType() {
		return aggregateType;
	}

	public void setAggregateType(String aggregateType) {
		this.aggregateType = aggregateType;
	}

	public String getEventClassName() {
		return eventClassName;
	}

	public void setEventClassName(String eventClassName) {
		this.eventClassName = eventClassName;
	}
	
	
	
	

}
