package com.cqrs.assertj;

import org.assertj.core.api.AbstractAssert;

import com.cqrs.domain.DomainException;
import com.cqrs.messaging.ID;

public class DomainExceptionAssert extends AbstractAssert<DomainExceptionAssert, DomainException> {

    DomainExceptionAssert(DomainException actual) {
        super(actual, DomainExceptionAssert.class);
    }

    public DomainExceptionAssert hasAggregateId(ID id) {
        isNotNull();
        if (!actual.getAggregateId().equals(id)) {
            failWithMessage("Expected aggregate id %s but got %s", id, actual.getAggregateId());
        }
        return this;
    }

    public DomainExceptionAssert hasMessage(String message) {
        isNotNull();
        if (!actual.getMessage().equals(message)) {
            failWithMessage("Expected message %s but got %s", message, actual.getMessage());
        }
        return this;
    }

    public DomainExceptionAssert messageContains(String message) {
        isNotNull();
        if (!actual.getMessage().contains(message)) {
            failWithMessage("Expected message to contain %s but got %s", message, actual.getMessage());
        }
        return this;
    }
}
