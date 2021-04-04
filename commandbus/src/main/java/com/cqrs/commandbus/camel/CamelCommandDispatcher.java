package com.cqrs.commandbus.camel;

import com.cqrs.commandbus.CommandDispatcher;
import com.cqrs.messaging.Command;
import com.cqrs.messaging.MessagingException;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

public class CamelCommandDispatcher implements CommandDispatcher {

    public static final String ENDPOINT_DISPATCH_COMMAND = "direct:dispatchCommand";
    private final ProducerTemplate producer;

    public CamelCommandDispatcher(CamelContext camelContext) {
        try {
            this.producer = camelContext.createProducerTemplate();
        } catch (Exception e) {
            throw new MessagingException(e);
        }
    }

    @Override
    public <T extends Command> void dispatch(T command) {
        producer.sendBody(ENDPOINT_DISPATCH_COMMAND, command);
    }
}
