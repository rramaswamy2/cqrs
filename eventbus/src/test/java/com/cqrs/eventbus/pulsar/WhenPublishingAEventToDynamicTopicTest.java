package com.cqrs.eventbus.pulsar;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.cqrs.eventbus.events.PulsarTestEvent;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.EventHandler;

	//@BeforeAll
	//void setUp() throws Exception {
/*		public WhenPublishingAEventToDynamicTopicTest() throws Exception {
		 PulsarConfig config = new PulsarConfig(PULSAR_LOCALHOST_6650,"shared");
 	   	
	        ActionHandlerResolver resolver = new ActionHandlerResolver();
	        resolver.registerActionHandler(this);
	        
	        String eventStreamId = UUID.randomUUID().toString();
	        toBePublished1 = new PulsarTestEvent(eventStreamId, "shoppingcart", "Apache Pulsar Awesomeness has happened using dynamic topics", 1);
	       // toBePublished = new PulsarTestEvent("00100de7-43e4-41ed-8759-061a7212ebcc", "shoppingcart", "Apache Pulsar Awesome", 1);
	        System.out.println("event to publish to pulsar : " + toBePublished1.toString());

	        publisher = new PulsarEventPublisher(config.getServiceUrl());
	        //publisher.publish("TestTopic", toBePublished);
	        publisher.publish(toBePublished1);
	        System.out.println("pulsar test event published one to save dynamic topic for an event stream ID/aggregate instance : " + toBePublished1.toString());
      
	        consumer = new PulsarEventConsumer(config,resolver);
            consumer.startSubscribeToTopicsInNamespace("persistent://public/default/shoppingcart-.*");
	        
            toBePublished2 = new PulsarTestEvent(eventStreamId, "shoppingcart" , "Apache Pulsar Awesomeness has happened again using dynamic topics", 2);
            publisher.publish(toBePublished2);
            System.out.println("pulsar test event published with an existing event stream ID will be : " + toBePublished2.toString());
	        await().atMost(5, SECONDS).until(() -> published != null);
	}

	/*@AfterEach
	void tearDown() throws Exception {
	}*/

/*	@Test
	void test() {
		
	System.out.println("to be published event to pulsar : " + toBePublished2.toString());
	
	System.out.println("published event from pulsar : " + published.toString());
	Assertions.assertNotNull(published, "published event not received at event handler..");
	Assertions.assertEquals(published.getStreamId().toString(), toBePublished2.getStreamId().toString());
	Assertions.assertEquals(published.getEventId().toString(), toBePublished2.getEventId().toString());
	Assertions.assertEquals(published.getEventDescription().toString(), toBePublished2.getEventDescription().toString());
	
	Assertions.assertEquals(toBePublished2.toString(), published.toString());
	
	
	
	}
	
	 @AfterAll
	    private void tearDown() {
	        try {
	        	System.out.println("closing producer and consumer..");
	            consumer.close();
	            publisher.close();
	        } catch (Exception e) {

	        }
	    }


} */
