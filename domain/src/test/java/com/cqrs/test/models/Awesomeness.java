package com.cqrs.test.models;

import com.cqrs.domain.AggregateRoot;
import com.cqrs.messaging.ID;
import com.cqrs.test.events.AwesomenessAdded;
import com.cqrs.test.events.AwesomenessCreated;

public class Awesomeness extends AggregateRoot {

    private String stuff;

    private Awesomeness() { super(); }

    public Awesomeness(ID id, String stuff) {
        super(id);
        this.applyChange(new AwesomenessCreated(id, stuff));
    }

    public void addMore(String moreStuff) {
        this.applyChange(new AwesomenessAdded(this.getId(), moreStuff));
    }

    protected void handle(AwesomenessCreated event) {
        this.setId(event.getStreamId());
        stuff = event.stuff;
    }

    protected void handle(AwesomenessAdded event) {
        stuff += event.stuff;
    }
}
