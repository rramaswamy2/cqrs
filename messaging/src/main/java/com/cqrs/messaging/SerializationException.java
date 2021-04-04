package com.cqrs.messaging;

public class SerializationException extends RuntimeException {

    public SerializationException(Exception cause) {
        super (cause);
    }
}
