package com.cqrs.assertj;

import com.cqrs.domain.DomainException;
import com.cqrs.messaging.Event;

public class Assertions {

    private Assertions() {
        
    }

    public static EventAssert assertThat(Event actual) {
        return new EventAssert(actual);
    }

    public static DomainExceptionAssert assertThat(DomainException actual) {
        return new DomainExceptionAssert(actual);
    }
}
