package com.cqrs.example.taskmanager.consumer.task;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import com.cqrs.example.taskmanager.consumer.bootstrap.ActionHandlerResolverConfiguration;
import com.cqrs.example.taskmanager.consumer.task.TaskRepository;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.Event;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public abstract class ProjectionTest<TEvent extends Event> {
    @Mock
    protected TaskRepository repository;

    protected ActionHandlerResolver actionHandlerResolver;
    
    protected TEvent event;

    @BeforeAll
    void setUp() {
        given();
        this.event = this.when();
        ActionHandlerResolverConfiguration actionHandlerResolverConfiguration = new ActionHandlerResolverConfiguration(repository);
        this.actionHandlerResolver = actionHandlerResolverConfiguration.actionHandlerResolver();
        invokeAccordingHandler(this.event);
    }

    protected abstract void given();

    protected abstract TEvent when();

    private void invokeAccordingHandler(TEvent event) {
        this.actionHandlerResolver.findHandlersFor(event.getClass().getSimpleName())
            .forEach(handler -> handler.handle(this.event));
    }
}
