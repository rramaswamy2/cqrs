package com.cqrs.junit;

import com.cqrs.commandbus.CommandDispatcher;
import com.cqrs.commandbus.local.SimpleCommandDispatcher;
import com.cqrs.domain.AggregateRoot;
import com.cqrs.domain.Repository;
import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventbus.local.SimpleEventPublisher;
import com.cqrs.eventstore.EventStore;
import com.cqrs.eventstore.local.InMemoryEventStore;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.Command;
import com.cqrs.messaging.CommandHandler;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.EventHandler;

public abstract class SingleEventTest<A extends AggregateRoot, C extends Command, E extends Event>
    implements EventHandler<E> {

    private C command;
    private E publishedEvent;
    private Exception caughtException;

    public SingleEventTest() {
        CommandDispatcher dispatcher = bootstrap();
        command = when();
        try {
            dispatcher.dispatch(command);
        } catch(Exception e) {
            caughtException = e;
        }
    }

    protected abstract CommandHandler commandHandlerFactory(Repository<A> repository);
    protected abstract Repository<A> repositoryFactory(EventStore eventStore, EventPublisher publisher);

    protected abstract void given(EventStore eventStore);
    protected abstract C when();

    private CommandDispatcher bootstrap() {
        ActionHandlerResolver eventResolver = new ActionHandlerResolver();
        eventResolver.registerActionHandler(this);

        EventStore eventStore = new InMemoryEventStore();
        given(eventStore);

        EventPublisher publisher = new SimpleEventPublisher(eventResolver);

        Repository<A> repository = repositoryFactory(eventStore, publisher);
        ActionHandlerResolver commandResolver = new ActionHandlerResolver();
        commandResolver.registerActionHandler(commandHandlerFactory(repository));

        return new SimpleCommandDispatcher(commandResolver);
    }

    @Override
    public void handle(E event) {
        publishedEvent = event;
    }

    protected C getCommand() {
        return command;
    }

    protected E getPublishedEvent() {
        return publishedEvent;
    }

    protected <T extends Exception> T getCaughtException() {
        return (T)caughtException;
    }
}
