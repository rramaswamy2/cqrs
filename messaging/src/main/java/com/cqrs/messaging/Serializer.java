package com.cqrs.messaging;

public interface Serializer {
    <T> String serialize(T object);

}
