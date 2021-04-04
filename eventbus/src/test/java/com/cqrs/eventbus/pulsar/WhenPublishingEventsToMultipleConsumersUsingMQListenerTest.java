package com.cqrs.eventbus.pulsar;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.cqrs.eventbus.events.PulsarTestEvent;

/* class WhenPublishingEventsToMultipleConsumersUsingMQListenerTest {

	private static final String SUBSCRIPTION_TYPE = "shared";
	private static final int NUM_MESSAGES = 12;
	private static final int NUM_CONSUMERS = 4;
	private static final int NUM_RECV_MESSAGES_PER_CONSUMER  = NUM_MESSAGES / NUM_CONSUMERS; 
	private static String APACHE_PULSAR_AWESOMENESS_HAS_HAPPENED = "Apache Pulsar Awesomeness has happened";
	private static final String TEST_TOPIC = "test_topic_3";
	private static final String PULSAR_LOCALHOST_6650 = "pulsar://localhost:6650";
	private static final String PULSAR_LOCALHOST_8080 = "http://localhost:8080";
	private static List<PulsarEventConsumer> consumerList = new ArrayList<PulsarEventConsumer>();
	private static PulsarEventPublisher publisher;
	private static Map<String, List<String>> consumerMap = new HashMap<String, List<String>>();
	
	private static class MQListener implements MessageListener<String> {

		private static final long serialVersionUID = 1L;

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void received(Consumer<String> consumer, Message<String> msg) {
			System.out.println("Message with key " + msg.getKey() + " and value " + msg.getValue() + " received by consumer");
			System.out.println("event received : " + msg.getValue());
			
			if(consumerMap.containsKey(consumer.getConsumerName())) {
				
				consumerMap.get(consumer.getConsumerName()).add(msg.getValue());
			} else {
				List<String> messageList = new ArrayList<String>();
				messageList.add(msg.getValue());
				consumerMap.put(consumer.getConsumerName(), messageList);
			}

			try {
				consumer.acknowledge(msg);
			} catch (PulsarClientException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static MQListener MQListener = new MQListener();
	
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		consumerMap.clear();
	    for(int i=0; i< NUM_CONSUMERS; i++) {
			PulsarEventConsumer consumer = new PulsarEventConsumer(PULSAR_LOCALHOST_6650, "Consumer-"+i);
			consumerList.add(consumer);
			consumer.startSubscribeToTopicAndSetMessageListener(TEST_TOPIC, SUBSCRIPTION_TYPE, MQListener);
			System.out.println("started " + consumer.getConsumerName() + " to subscribe to topic : " + TEST_TOPIC);
			
			}
		
		
		publisher = new PulsarEventPublisher(PULSAR_LOCALHOST_6650);
		for(int i=0; i< NUM_MESSAGES; i++) {
		PulsarTestEvent toBePublished = new PulsarTestEvent(UUID.randomUUID().toString(), APACHE_PULSAR_AWESOMENESS_HAS_HAPPENED +" "+ i , i);
		publisher.publish(TEST_TOPIC, toBePublished);
		System.out.println("pulsar test event published : " + toBePublished.toString());
		}
		// wait few seconds until consumer map is populated fully..
		await().atLeast(1, SECONDS).pollDelay(1, SECONDS).until(() -> true);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		for(PulsarEventConsumer consumer : consumerList) consumer.close();
		publisher.close();
	}

	@Test
	void test() {
		for(Map.Entry<String,List<String>> consumerMapEntry : consumerMap.entrySet()) {
	        	String consumerName = consumerMapEntry.getKey();
	        	List<String> consumerMessageList  = consumerMapEntry.getValue();
	        	for(String message : consumerMessageList) {
	        		System.out.println("message received by consumer : " + consumerName + " is : " + message);
	        	}
	        	//assertEquals(NUM_RECV_MESSAGES_PER_CONSUMER, consumerMessageList.size(), "Failed to receive expected number of messages");
	        }
		
	}

} */
