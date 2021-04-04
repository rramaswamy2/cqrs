package com.cqrs.commandbus.local;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cqrs.commandbus.commands.TestCommand;
import com.cqrs.commandbus.local.SimpleCommandDispatcher;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.CommandHandler;

public class WhenDispatchingACommandTest implements CommandHandler<TestCommand> {

    private final SimpleCommandDispatcher dispatcher;
    private final TestCommand toBeDispatched;
    private TestCommand dispatchedCommand;

   

    @Override
    public void handle(TestCommand command) {
        dispatchedCommand = command;
    }

    public WhenDispatchingACommandTest() {
        ActionHandlerResolver resolver = new ActionHandlerResolver();
        resolver.registerActionHandler(this);
        dispatcher = new SimpleCommandDispatcher(resolver);
        toBeDispatched = new TestCommand(UUID.randomUUID(), "Awesomeness has happened");
        dispatcher.dispatch(toBeDispatched);
    }

    @Test
    public void testItShouldDispatchTheCommandToTheRegisteredCommandHandler() {
        assertEquals(dispatchedCommand, toBeDispatched, "The dispatched command is not received in the registered handler");
    }

    @Test
    public void testItShouldHaveTheSameValuesForProperties() {
        assertEquals(dispatchedCommand.getId(), toBeDispatched.getId(), "The dispatched command id value has changed");
        assertEquals(dispatchedCommand.getStuff(), toBeDispatched.getStuff(), "The dispatched command stuff value has changed");
    }
}
