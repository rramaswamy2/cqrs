package com.cqrs.eventstore;

import java.io.Serializable;
import java.util.UUID;

import com.cqrs.messaging.Event;
import com.cqrs.messaging.ID;

public class TestEventV2 extends Event implements Serializable {
    
    private static final long serialVersionUID = 1L;

    public final String stuff;
    
    public String stuff2;
    
    public TestEventV2() {
        this(null, null, 0);
    }

    public TestEventV2(ID id, String stuff, int version) {
        super();
        this.setStreamId(id);
        this.setEventId(ID.fromObject(UUID.randomUUID()));
        this.setVersion(version);
        this.stuff = stuff;
    }

    public String getStuff2() {
        return stuff2;
    }

    public void setStuff2(String stuff2) {
        this.stuff2 = stuff2;
    }

    public String getStuff() {
        return stuff;
    }
    
    

}
