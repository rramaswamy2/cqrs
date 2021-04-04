package com.cqrs.eventbus.pulsar;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.apache.pulsar.client.api.SubscriptionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cqrs.messaging.ActionHandler;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.JsonDeserializer;

public class PulsarEventConsumer {

	private static final String MY_SUBSCRIPTION = "my-subscription";
	private static final String FAILOVER = "failover";
	private static final String KEY_SHARED = "key_shared";
	private static final String SHARED = "shared";
	//private static final String PULSAR_LOCALHOST = "http://localhost:8080";
	private static final String PULSAR_LOCALHOST = "pulsar://localhost:6650";
	private static final Logger LOG = LoggerFactory.getLogger(PulsarEventConsumer.class);
	private PulsarClient client;
	private Consumer<String> consumer;
	private ConsumerBuilder<String> consumerBuilder;
	private static JsonDeserializer deser = new JsonDeserializer();
	private String consumerName;
	private String topicName;
	private String serviceUrl;
	private String subscriptionType;
	private ActionHandlerResolver eventResolver;
    
	private class MQListener implements MessageListener<String> {

		private static final long serialVersionUID = 1L;

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void received(Consumer<String> consumer, Message<String> msg) {
			LOG.info("Message with key {} and value {} received by consumer", msg.getKey(), msg.getValue());
			LOG.info("event to process : " + msg.getValue());
			Map<String, String> jsonMap = deser.deserializeJsonToMap(msg.getValue());
			String eventType = jsonMap.get("eventType");
			List<ActionHandler> eventHandlers = eventResolver.findHandlersFor(eventType);
			Class<?> eventClazz = eventResolver.getHandledActionType(eventHandlers.get(0).getClass());
			Event event = (Event) deser.deserialize(msg.getValue(), eventClazz);
			eventHandlers.forEach(handler -> handler.handle(event));

			try {
				consumer.acknowledge(msg);
			} catch (PulsarClientException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private final MQListener mqListener = this.new MQListener();

	public PulsarEventConsumer(String topicName, ActionHandlerResolver resolver) throws PulsarClientException {
		this(topicName, PULSAR_LOCALHOST, SHARED, resolver);
	}
	
	// use this constructor to unit test the consumer with consumer.receive or consumer.receiveAsync API without wiring to a event handler using ActionHandlerResolver
	// default subscription type of EXCLUSIVE will be used here
	// to be used in conjunction with startSubscribeToTopicAndSyncReceive and startSubscribeToTopicAndAsyncReceive
	public PulsarEventConsumer(String serviceUrl) throws PulsarClientException {
	   this.serviceUrl = serviceUrl;
	   this.client = PulsarClient.builder().serviceUrl(serviceUrl).build();
	   
    }
	
	public PulsarEventConsumer(String serviceUrl, String consumerName) throws PulsarClientException {
		
		this.serviceUrl = serviceUrl;
		this.consumerName = consumerName;
		this.client = PulsarClient.builder().serviceUrl(serviceUrl).build();
	}

	public PulsarEventConsumer(String topicName, String serviceUrl, String subscriptionType,
			ActionHandlerResolver resolver) throws PulsarClientException {
		this.client = PulsarClient.builder().serviceUrl(serviceUrl).build();
		this.subscriptionType = subscriptionType;
		this.topicName = topicName;
		this.eventResolver = resolver;
		start(topicName);
	}

	public PulsarEventConsumer(PulsarConfig config, ActionHandlerResolver resolver) throws PulsarClientException {
		this.serviceUrl = config.getServiceUrl();
		this.subscriptionType = config.getSubscriptionType();
		this.eventResolver = resolver;
		this.client = PulsarClient.builder().serviceUrl(serviceUrl).build();
	}

	// use this method to unit test a pulsar consumer without wiring to a event handler with a consumer.receieve call 
	// has issue if topic does not exist or is not created upfront and the sync wait and receive will block indefinitely
	// for the first run 
	// subsequent runs will work fine 
	public String startSubscribeToTopicAndSyncReceive(String topicName) throws PulsarClientException {
		consumer = client.newConsumer(Schema.STRING).topic(topicName).subscriptionName(MY_SUBSCRIPTION).subscribe();
        Message<String> msg = consumer.receive();	
		String value = msg.getValue();
		consumer.acknowledge(msg);
		return value;
	}
	
	public void startSubscribeToTopicWithSharedSubscriptionMode(String topicName) throws PulsarClientException {
		consumer = client.newConsumer(Schema.STRING).topic(topicName).subscriptionName(MY_SUBSCRIPTION).subscriptionType(SubscriptionType.Shared).subscribe();
	}
	
	public void startSubscribeToTopicAndSetMessageListener(String topicName, String subscriptionType, MessageListener listener) throws PulsarClientException  {
		
		SubscriptionType subscribe_type = this.evalSubscriptionType(subscriptionType);
		consumer = client.newConsumer(Schema.STRING).consumerName(this.consumerName).topic(topicName).subscriptionName(MY_SUBSCRIPTION).subscriptionType(subscribe_type).messageListener(listener).subscribe();
		
	}
	
	public CompletableFuture<String> asyncReceive() {
		CompletableFuture<String> futureStr = consumer.receiveAsync().thenApply(msg -> { String value = msg.getValue(); 
		try {
			consumer.acknowledge(msg);
		} catch (PulsarClientException e) {
			LOG.error("failed to ACK message : " + e.getMessage());
			throw new RuntimeException(e);
		}
		return value; });
		return futureStr;

	}
	
	public CompletableFuture<String> startSubscribeToTopicAndAsyncReceive(String topicName) throws PulsarClientException  {
		consumer = client.newConsumer(Schema.STRING).topic(topicName).subscriptionName(MY_SUBSCRIPTION).subscriptionType(SubscriptionType.Shared).subscribe();
		CompletableFuture<String> futureStr = consumer.receiveAsync().thenApply(msg -> { String value = msg.getValue(); 
		try {
			consumer.acknowledge(msg);
		} catch (PulsarClientException e) {
			LOG.error("failed to ACK message : " + e.getMessage());
			throw new RuntimeException(e);
		}
		return value; });
		return futureStr;
	}
	
	public void startSubscribeToTopicsInNamespace(String namespacePattern) throws PulsarClientException {
		
		// expect pattern like "persistent://public/default/shoppingcart-.*"
		LOG.info("topics pattern to subscribe : " + namespacePattern);
		Pattern someTopicsInNamespace = Pattern.compile(namespacePattern);
		SubscriptionType subtype = evalSubscriptionType(this.subscriptionType);
		consumer =client.newConsumer(Schema.STRING).topicsPattern(someTopicsInNamespace).subscriptionName(MY_SUBSCRIPTION).subscriptionType(subtype).messageListener(mqListener).subscribe();
	    
	}

	public void start(String topicName) throws PulsarClientException {
		SubscriptionType subtype = evalSubscriptionType(this.subscriptionType);
		ConsumerBuilder<String> builder = client.newConsumer(Schema.STRING).topic(topicName)
				.subscriptionName(MY_SUBSCRIPTION).ackTimeout(10, TimeUnit.SECONDS).subscriptionType(subtype)
				.messageListener(mqListener); //avoid creating new inner class listener instance every time when subscribing to a topic..
				//.messageListener(new MQListener());
		consumer = builder.subscribe();

	}

	private SubscriptionType evalSubscriptionType(String subscribeType) {
		SubscriptionType subtype = null;
		if (subscribeType.equalsIgnoreCase(SHARED)) {
			subtype = SubscriptionType.Shared; // most common subscription mode or type for round robin dispatching..
		} else if (subscribeType.equalsIgnoreCase(KEY_SHARED)) {
			subtype = SubscriptionType.Key_Shared;
		} else if (subscribeType.equalsIgnoreCase(FAILOVER)) {
			subtype = SubscriptionType.Failover;
		} else {
			subtype = SubscriptionType.Exclusive; //default
		}
		return subtype;
	}
	
	

	public String getConsumerName() {
		return consumerName;
	}

	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(String subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	@Override
	public String toString() {
		return "PulsarEventConsumer [topicName=" + topicName + ", serviceUrl=" + serviceUrl + ", subscriptionType="
				+ subscriptionType + "]";
	}

	public void close() throws PulsarClientException {
		consumer.close();
		client.close();
	}

}
