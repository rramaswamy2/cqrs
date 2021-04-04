package com.cqrs.commandbus;

import com.cqrs.messaging.Command;

public interface CommandDispatcher {
    <T extends Command> void dispatch(T command);
}
