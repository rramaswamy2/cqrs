package com.cqrs.messaging;

import java.util.concurrent.TimeUnit;

public interface AggregateCache {
    
    Object put(String key, Object value);
    Object get(String key);
    boolean containsKey(String key);
    boolean expire(long ttl, TimeUnit unit);
    long remainingTTLForMapEntries();
    void shutdown();

}
