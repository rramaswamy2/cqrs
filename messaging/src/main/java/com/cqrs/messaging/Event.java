package com.cqrs.messaging;

public abstract class Event implements Action {
    private ID streamId;
    private ID eventId;
    private int version;

    public ID getStreamId() {
        return streamId;
    }

    public void setStreamId(ID streamId) {
        this.streamId = streamId;
    }

    public ID getEventId() {
        return eventId;
    }

    public void setEventId(ID eventId) {
        this.eventId = eventId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
