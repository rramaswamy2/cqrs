package com.cqrs.messaging;

public interface Deserializer {
    <T> T deserialize(String json, Class<T> clazz);

}
