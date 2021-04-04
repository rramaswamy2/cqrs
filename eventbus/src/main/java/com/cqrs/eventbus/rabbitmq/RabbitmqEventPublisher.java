package com.cqrs.eventbus.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventbus.serialize.EventEnvelope;
import com.cqrs.messaging.Deserializer;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.MessagingException;
import com.cqrs.messaging.Serializer;

public class RabbitmqEventPublisher implements EventPublisher, AutoCloseable {

    private final Connection connection;
    private final Channel channel;
    private final Serializer serializer;
    private final Deserializer deserializer;


    public RabbitmqEventPublisher(RabbitMqConfig config, Serializer serializer, Deserializer deserializer) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(config.getHost());
            factory.setPort(config.getPort());
            factory.setVirtualHost(config.getVhost());
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
            this.serializer = serializer;
            this.deserializer = deserializer;
        } catch (TimeoutException | IOException e) {
            throw new MessagingException(e);
        }
    }

    @Override
    public <T extends Event> void publish(String streamName, T event) {
        try {
            String serializedEvent = serializer.serialize(EventEnvelope.fromEvent(event, this.serializer, this.deserializer));
            channel.exchangeDeclare(streamName, "topic", true);
            channel.basicPublish(streamName, event.getClass().getSimpleName(), null, serializedEvent.getBytes());

        } catch (IOException e) {
            throw new MessagingException(e);
        }
    }

    @Override
    public <T extends Event> void publish(String streamName, Iterable<T> events) {
        events.forEach(event -> publish(streamName, event));
    }

    @Override
    public void close() throws Exception {
        channel.close();
        connection.close();
    }
}
