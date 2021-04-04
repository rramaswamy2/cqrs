package com.cqrs.eventbus.pulsar;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.cqrs.eventbus.events.PulsarTestEvent;

/* class WhenPublishingEventsToMultipleConsumerTest {

	private static final int NUM_MESSAGES = 12;
	private static final int NUM_CONSUMERS = 4;
	private static final int NUM_RECV_MESSAGES_PER_CONSUMER  = NUM_MESSAGES / NUM_CONSUMERS; 
	private static String APACHE_PULSAR_AWESOMENESS_HAS_HAPPENED = "Apache Pulsar Awesomeness has happened";
	private static final String TEST_TOPIC = "test_topic";
	private static final String PULSAR_LOCALHOST_6650 = "pulsar://localhost:6650";
	private static final String PULSAR_LOCALHOST_8080 = "http://localhost:8080";
	//private static List<PulsarTestEvent> toBePublishedList = new ArrayList<PulsarTestEvent>();
	private static List<PulsarEventConsumer> consumerList = new ArrayList<PulsarEventConsumer>();
	private static PulsarEventPublisher publisher;
	
	private static Map<String, List<CompletableFuture<String>>> consumerMessageMap = new HashMap<String, List<CompletableFuture<String>>>();
		
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		consumerMessageMap.clear();
		for(int i=0; i< NUM_CONSUMERS; i++) {
		PulsarEventConsumer consumer = new PulsarEventConsumer(PULSAR_LOCALHOST_6650, "Consumer-"+i);
		consumerList.add(consumer);
		consumer.startSubscribeToTopicWithSharedSubscriptionMode(TEST_TOPIC);
		System.out.println("started " + consumer.getConsumerName() + " to subscribe to topic : " + TEST_TOPIC);
		
		}
		for(PulsarEventConsumer consumer : consumerList) {
		List<CompletableFuture<String>> futureJsonStrList = new ArrayList<CompletableFuture<String>>();
		for(int i=0; i< NUM_RECV_MESSAGES_PER_CONSUMER; i++) {
			
		futureJsonStrList.add(consumer.asyncReceive());
		}
		
		System.out.println("future message list size for consumer : " + consumer.getConsumerName() +" is : " + futureJsonStrList.size());
		consumerMessageMap.put(consumer.getConsumerName(), futureJsonStrList);
	
		}
		
		System.out.println("consumer initial message map : " + consumerMessageMap.toString());

		publisher = new PulsarEventPublisher(PULSAR_LOCALHOST_6650);
		for(int i=0; i< NUM_MESSAGES; i++) {
		PulsarTestEvent toBePublished = new PulsarTestEvent(UUID.randomUUID().toString(), APACHE_PULSAR_AWESOMENESS_HAS_HAPPENED +" "+ i , i);
		//toBePublishedList.add(toBePublished);
		publisher.publish(TEST_TOPIC, toBePublished);
		System.out.println("pulsar test event published : " + toBePublished.toString());
		}
		//System.out.println("event list published to pulsar : " + toBePublishedList.toString());
		
		// no need to wait as we are using async receive and CompletableFuture API here.. Future.get will block till results available..
		// wait few seconds for the future data structure to get populated with asyncReceive messages..
		// await().atLeast(4, SECONDS).pollDelay(4, SECONDS).until(() -> true);
		
	}

	@Test
	void test() throws InterruptedException, ExecutionException {
		  for(Map.Entry<String,List<CompletableFuture<String>>> consumerMessageMapEntry : consumerMessageMap.entrySet()) {
	        	String consumerName = consumerMessageMapEntry.getKey();
	        	List<CompletableFuture<String>> consumerFutureMessageList = consumerMessageMapEntry.getValue();
	        	for(CompletableFuture<String> futureJsonStr : consumerFutureMessageList) {
	        		System.out.println("message received by consumer : " + consumerName + " is : " + futureJsonStr.get());
	        	}
	        	//assertEquals(NUM_RECV_MESSAGES_PER_CONSUMER, consumerFutureMessageList.size(), "Failed to receive expected number of messages");
	        }
	}
	
	@AfterAll
	static void tearDownAfterClass() throws Exception {
		for(PulsarEventConsumer consumer : consumerList) consumer.close();
		publisher.close();
	}


} */
