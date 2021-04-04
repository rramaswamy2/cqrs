package com.cqrs.eventbus.rabbitmq;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqrs.eventbus.serialize.EventEnvelope;
import com.cqrs.messaging.ActionHandler;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.Deserializer;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.MessagingException;
import com.cqrs.messaging.Serializer;

public class RabbitmqEventConsumer implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(RabbitmqEventConsumer.class);

    private final Connection connection;
    private final Channel channel;
    private final Serializer serializer;
    private final Deserializer deserializer;
    private final ActionHandlerResolver resolver;

    public RabbitmqEventConsumer(RabbitMqConfig config, Deserializer deserializer, Serializer serializer, ActionHandlerResolver resolver) {
        this.deserializer = deserializer;
        this.serializer = serializer;
        this.resolver = resolver;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(config.getHost());
            factory.setPort(config.getPort());
            factory.setVirtualHost(config.getVhost());
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
        } catch (TimeoutException | IOException e) {
            throw new MessagingException(e);
        }
    }

    @Override
    public void close() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    public void start(String streamName) {
        try {
            Consumer consumer = newConsumer(channel);

            String queueName = channel.queueDeclare().getQueue();

            channel.exchangeDeclare(streamName, "topic", true);

            resolver.getSupportedActions()
                    .forEach(e -> subscribeEvent(queueName, streamName, e));

            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            throw new MessagingException(e);
        }
    }

    private void subscribeEvent(String queueName, String streamName, String eventType) {
        try {
            channel.queueBind(queueName, streamName, eventType);
        } catch (IOException e) {
            throw new MessagingException(e);
        }
    }

    private Consumer newConsumer(Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {

                String message = new String(body, "UTF-8");
                tryHandleAction(envelope.getRoutingKey(), message);
                if (LOG.isInfoEnabled()) {
                    LOG.info(" [x] Received '{}': '{}'", envelope.getRoutingKey(), message);
                }
            }
        };
    }

    private void tryHandleAction(String action, String message) {
        try {
            handleAction(action, message);
        } catch (Exception ex) {
            handleException(action, ex);
        }
    }

    private void handleAction(String action, String value) {
        List<ActionHandler> handlers = resolver.findHandlersFor(action);
        Class<?> clazz = resolver.getHandledActionType(handlers.get(0).getClass());
        EventEnvelope eventClazz = deserializer.deserialize(value, EventEnvelope.class);
        Event event = (Event) this.deserializer.deserialize(this.serializer.serialize(eventClazz.eventData), clazz);
        handlers.forEach(handler -> handler.handle(event));
    }

    private void handleException(String action, Exception ex) {
        LOG.error("Application error while handling the action: [{}]", action, ex);
    }
}
