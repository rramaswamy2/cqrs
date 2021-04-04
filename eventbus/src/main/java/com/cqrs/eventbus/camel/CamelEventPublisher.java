package com.cqrs.eventbus.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

import com.cqrs.eventbus.EventPublisher;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.MessagingException;
import com.cqrs.messaging.Serializer;

public class CamelEventPublisher implements EventPublisher {

    private final ProducerTemplate producer;

    private Serializer serializer;

    public CamelEventPublisher(CamelContext camelContext, Serializer serializer) {
        this.serializer = serializer;
        try {
            this.producer = camelContext.createProducerTemplate();
        } catch (Exception e) {
            throw new MessagingException(e);
        }
    }

    @Override
    public <T extends Event> void publish(String streamName, T event) {
        producer.sendBody(streamName, this.serializer.serialize(event));
    }

    @Override
    public <T extends Event> void publish(String streamName, Iterable<T> events) {
        events.forEach(event -> publish(streamName, event));
    }
}
