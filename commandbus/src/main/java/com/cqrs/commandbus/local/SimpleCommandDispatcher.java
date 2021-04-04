package com.cqrs.commandbus.local;

import java.util.List;

import com.cqrs.commandbus.CommandDispatcher;
import com.cqrs.messaging.ActionHandler;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.Command;

public class SimpleCommandDispatcher implements CommandDispatcher {
    private final ActionHandlerResolver resolverProvider;

    public SimpleCommandDispatcher(ActionHandlerResolver resolverProvider) {
        this.resolverProvider = resolverProvider;
    }

    @Override
    public <T extends Command> void dispatch(T command) {
        List<ActionHandler> handlers = resolverProvider.findHandlersFor(command.getClass().getSimpleName());

        if (handlers != null) {
            for (ActionHandler<T> handler : handlers) {
                handler.handle(command);
            }
        }
    }
}
