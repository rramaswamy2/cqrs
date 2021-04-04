package com.cqrs.domain;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.cqrs.domain.AggregateRepository;
import com.cqrs.domain.Repository;
import com.cqrs.eventbus.local.SimpleEventPublisher;
import com.cqrs.eventstore.local.InMemoryEventStore;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.EventHandler;
import com.cqrs.messaging.ID;
import com.cqrs.test.events.AwesomenessCreated;
import com.cqrs.test.models.Awesomeness;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("When getting an aggregate by id")
public class WhenGettingAnAggregateByIdTest implements EventHandler<AwesomenessCreated> {

    private final Repository<Awesomeness> repository;
    private final Awesomeness storedAwesomeness;
    private final ID awesomenessId;
    private AwesomenessCreated publishedEvent;

    @Override
    public void handle(AwesomenessCreated event) {
        publishedEvent = event;
    }

    public WhenGettingAnAggregateByIdTest() {
        ActionHandlerResolver resolver = new ActionHandlerResolver();
        resolver.registerActionHandler(this);

        InMemoryEventStore inMemoryEventStore = new InMemoryEventStore();
        awesomenessId = ID.fromObject(UUID.randomUUID());

        Iterable<Event> domainEvents = new ArrayList<Event>() {{ add(new AwesomenessCreated(awesomenessId, "This is really amazing stuff")); }};

        inMemoryEventStore.save(Awesomeness.class.getSimpleName(), awesomenessId.toString(), domainEvents, 0);
        repository = new AggregateRepository<Awesomeness>(inMemoryEventStore, new SimpleEventPublisher(resolver)) {
            @Override
            public void save(Awesomeness aggregate, int version) {
                super.save(aggregate, version);
            }
        };
        storedAwesomeness = repository.getById(awesomenessId);
    }

    @Test
    public void testTheAggregateShouldBeStoredInTheEventStore() {
        assertNotNull(storedAwesomeness, "The aggregate can not be found in the eventstore");
    }

    @Test
    public void testTheAggregateShouldHaveTheOriginalId() {
        assertEquals(awesomenessId, storedAwesomeness.getId(), "The aggregate can not be found in the eventstore");
    }

    @Test
    public void testTheAggregateShouldHaveVersionOne() {
        assertEquals(1, storedAwesomeness.getVersion(), "The aggregate can not be found in the eventstore");
    }

    @Test
    public void testNoEventsShouldBePublished() {
        assertNull(publishedEvent, "An event was published to an eventhandler");
    }
}
