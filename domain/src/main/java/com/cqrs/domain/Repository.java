package com.cqrs.domain;

import java.util.List;

import com.cqrs.messaging.ID;

public interface Repository<T extends AggregateRoot> {
    void save(T aggregate, int version);
    T getById(ID id);
    void replay(ID id);
    List<String> replayAll();
}
