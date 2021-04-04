package com.cqrs.messaging;

public interface AggregateLettuceCache {
    
    String set(String key, String value);
    String get(String key);
    Long ttl(String key);
    Long exists(String key);
    Boolean expire(String key, long timeout);
    

}
