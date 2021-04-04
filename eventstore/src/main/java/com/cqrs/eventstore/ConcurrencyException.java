package com.cqrs.eventstore;

public class ConcurrencyException extends RuntimeException {

    private final String streamId;
    private final int actualVersion;
    private final int expectedVersion;

    public ConcurrencyException(String streamId, int actualVersion, int expectedVersion) {
        super(String.format(
                "Stream with id: '%s' has already been committed with version '%d', we expected it to be '%d'.",
                streamId, actualVersion, expectedVersion));
        this.streamId = streamId;
        this.actualVersion = actualVersion;
        this.expectedVersion = expectedVersion;
    }

    public String getStreamId() {
        return streamId;
    }

    public int getActualVersion() {
        return actualVersion;
    }

    public int getExpectedVersion() {
        return expectedVersion;
    }
}
