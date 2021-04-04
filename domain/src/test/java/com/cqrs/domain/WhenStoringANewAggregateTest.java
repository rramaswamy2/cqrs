package com.cqrs.domain;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.cqrs.domain.AggregateRepository;
import com.cqrs.domain.Repository;
import com.cqrs.eventbus.local.SimpleEventPublisher;
import com.cqrs.eventstore.local.InMemoryEventStore;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.EventHandler;
import com.cqrs.messaging.ID;
import com.cqrs.test.events.AwesomenessCreated;
import com.cqrs.test.models.Awesomeness;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("When storin a new aggregate")
public class WhenStoringANewAggregateTest implements EventHandler<AwesomenessCreated> {

    private final Repository<Awesomeness> repository;
    private final Awesomeness awesomenessToBeStored;
    private AwesomenessCreated publishedEvent;

    @Override
    public void handle(AwesomenessCreated event) {
        publishedEvent = event;
    }

    public WhenStoringANewAggregateTest() {
        ActionHandlerResolver resolver = new ActionHandlerResolver();
        resolver.registerActionHandler(this);

        repository = new AggregateRepository<Awesomeness>(new InMemoryEventStore(), new SimpleEventPublisher(resolver)) {
            @Override
            public void save(Awesomeness aggregate, int version) {
                super.save(aggregate, version);
            }
        };
        awesomenessToBeStored = new Awesomeness(ID.fromObject(UUID.randomUUID()), "This is really amazing stuff");
        repository.save(awesomenessToBeStored, awesomenessToBeStored.getVersion());
    }

    @Test
    public void testTheAggregateShouldBeStoredInTheEventStore() {
        Awesomeness storedAwesomeness = repository.getById(awesomenessToBeStored.getId());
        assertNotNull(storedAwesomeness, "The aggregate can not be found in the eventstore");
    }

    @Test
    public void testTheAggregateShouldHaveTheOriginalId() {
        Awesomeness storedAwesomeness = repository.getById(awesomenessToBeStored.getId());
        assertEquals(awesomenessToBeStored.getId(), storedAwesomeness.getId(), "The aggregate can not be found in the eventstore");
    }

    @Test
    public void testTheAggregateShouldHaveAnUpdatedVersion() {
        Awesomeness storedAwesomeness = repository.getById(awesomenessToBeStored.getId());
        assertEquals(awesomenessToBeStored.getVersion() + 1, storedAwesomeness.getVersion(), "The aggregate can not be found in the eventstore");
    }

    @Test
    public void testTheEventShouldBePublished() {
        assertNotNull(publishedEvent, "The event was not published to an eventhandler");
    }
}
