package com.cqrs.eventbus.events;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import com.cqrs.eventbus.serialize.PulsarEvent;
import com.cqrs.messaging.ID;

public class PulsarTestEvent extends PulsarEvent implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String eventDescription;

	public PulsarTestEvent() {
		super();
	}
	
	public PulsarTestEvent(String streamId, String aggregateType, String eventDescr, int version) {
	this(streamId,eventDescr,version);
	this.setAggregateType(aggregateType);	
		
	}
	
	public PulsarTestEvent(String streamId, String eventDescr, int version) {
		this.setStreamId(ID.fromObject(streamId));
		this.setEventId(ID.fromObject(UUID.randomUUID().toString()));
		this.setVersion(version);
		this.setTimestamp(Instant.now().toString());
		this.setEventType(this.getClass().getSimpleName());
		this.setEventClassName(this.getClass().getCanonicalName());  // or just this.getCLass().getName() should be sufficient
		this.eventDescription = eventDescr;
	}
	

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	@Override
	public String toString() {
		return "PulsarTestEvent [eventDescription=" + eventDescription + ", getTimestamp()=" + getTimestamp()
				+ ", getEventType()=" + getEventType() + ", getAggregateType()=" + getAggregateType()
				+ ", getEventClassName()=" + getEventClassName() + ", getStreamId()=" + getStreamId()
				+ ", getEventId()=" + getEventId() + ", getVersion()=" + getVersion() + "]";
	}

	

	

}
