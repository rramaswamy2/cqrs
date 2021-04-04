package com.cqrs.domain;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.cqrs.domain.AggregateNotFoundException;
import com.cqrs.domain.AggregateRepository;
import com.cqrs.domain.Repository;
import com.cqrs.eventbus.local.SimpleEventPublisher;
import com.cqrs.eventstore.local.InMemoryEventStore;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.ID;
import com.cqrs.test.models.Awesomeness;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("When querying a non existing aggregate")
public class WhenQueryingANonExistingAggregateTest {

    private final Repository<Awesomeness> repository;
    private Awesomeness storedAwesomeness;
    private final ID awesomenessId;
    private AggregateNotFoundException caughtException;

    public WhenQueryingANonExistingAggregateTest() {

        InMemoryEventStore inMemoryEventStore = new InMemoryEventStore();
        awesomenessId = ID.fromObject(UUID.randomUUID());

        repository = new AggregateRepository<Awesomeness>(inMemoryEventStore, new SimpleEventPublisher(new ActionHandlerResolver())) {
            @Override
            public void save(Awesomeness aggregate, int version) {
                super.save(aggregate, version);
            }
        };
        try {
            storedAwesomeness = repository.getById(awesomenessId);
        } catch (AggregateNotFoundException e) {
            caughtException = e;
        }
    }

    @Test
    public void testItShouldNotReturnAnAggregate() {
        assertNull(storedAwesomeness, "An instance of an aggregate was returned");
    }

    @Test
    public void testItShouldThrowAAggregateNotFoundException() {
        assertNotNull(caughtException, "The AggregateNotFoundException has not been thrown");
    }

    @Test
    public void testItShouldMentionTheAggregateTypeInTheMessage() {
        assertTrue(caughtException.getMessage().contains("Awesomeness"), "The exception message does not contain Awesomeness");
    }

    @Test
    public void testItShouldMentionTheAggregateIdInTheMessage() {
        assertTrue(caughtException.getMessage().contains(awesomenessId.toString()), "The exception message does not contain the id we where looking for");
    }
}

