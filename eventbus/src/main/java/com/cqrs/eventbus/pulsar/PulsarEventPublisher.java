package com.cqrs.eventbus.pulsar;

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.MessageRoutingMode;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.TypedMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventbus.serialize.PulsarEvent;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.JsonSerializer;
import com.cqrs.messaging.MessagingException;
import com.cqrs.messaging.Serializer;

public class PulsarEventPublisher implements EventPublisher {

	private static final Logger LOG = LoggerFactory.getLogger(PulsarEventPublisher.class);
	private Producer<String> producer;
	private static Serializer serializer = new JsonSerializer();
	private PulsarClient client;

	public PulsarEventPublisher(String serviceUrl) throws PulsarClientException {
		client = PulsarClient.builder().serviceUrl(serviceUrl).build();
	}

	public <T extends PulsarEvent> void publish(T event) {
		try {
			String topicName = event.getAggregateType() + "-" + event.getStreamId().toString();
			producer = client.newProducer(Schema.STRING).topic(topicName).create();
			String serializedEventJsonStr = serializer.serialize(event);
			String key = event.getStreamId().toString();
			TypedMessageBuilder<String> msgBuilder = producer.newMessage();
			MessageId msgId = msgBuilder.key(key).value(serializedEventJsonStr).send();
			LOG.info("published message to pulsar on topic : " + topicName + " with key : " + key + " and value : "
					+ serializedEventJsonStr + " with message ID : " + msgId.toString());
		} catch (PulsarClientException e) {
			throw new MessagingException(e);
		}

	}

	@Override
	public <T extends Event> void publish(String topicName, T event) {

		try {
			producer = client.newProducer(Schema.STRING).topic(topicName)
					.messageRoutingMode(MessageRoutingMode.RoundRobinPartition).enableBatching(false).create();
			String serializedJsonEventStr = serializer.serialize(event);
			String key = event.getStreamId().toString();
			MessageId msgId = producer.newMessage().key(key).value(serializedJsonEventStr).send();
			LOG.info("published message to pulsar with key : " + key + " and valaue : " + serializedJsonEventStr
					+ " with message ID: " + msgId.toString());

		} catch (PulsarClientException e) {
			throw new MessagingException(e);
		}

	}

	@Override
	public <T extends Event> void publish(String streamName, Iterable<T> events) {
		events.forEach(event -> publish(streamName, event));
	}

	public void close() throws PulsarClientException {
		producer.close();
		client.close();
	}

}
