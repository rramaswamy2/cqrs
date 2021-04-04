package com.cqrs.messaging;

public class MessagingException extends RuntimeException {

    public MessagingException(Throwable throwable) {
        super(throwable);
    }
}
