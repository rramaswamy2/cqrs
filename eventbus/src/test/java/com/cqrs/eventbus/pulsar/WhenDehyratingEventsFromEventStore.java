package com.cqrs.eventbus.pulsar;

import java.util.Iterator;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cqrs.eventbus.events.PulsarTestEvent;
import com.cqrs.eventstore.pulsar.PulsarEventStore;
import com.cqrs.messaging.Event;
	 
/*	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		  //String serviceUrl = "pulsar://localhost:6650";
          // String serviceUrl = "http://localhost:8080";
	    PulsarConfig config = new PulsarConfig(PULSAR_LOCALHOST_6650,"shared");
  	   	
	        //ActionHandlerResolver resolver = new ActionHandlerResolver();
	        //resolver.registerActionHandler(this);
	        String eventStreamId = UUID.randomUUID().toString();
	        toBePublished1 = new PulsarTestEvent(eventStreamId, "Apache Pulsar event store Awesomeness first", 1);
	        
	        toBePublished2 = new PulsarTestEvent(eventStreamId, "Apache Pulsar event store Awesomeness second", 2);
	        
	        toBePublished3 = new PulsarTestEvent(eventStreamId, "Apache Pulsar event store Awesomeness third", 3);

	        
	       // System.out.println("event to publish to pulsar : " + toBePublished1.toString());
	        
	        //consumer = new PulsarEventConsumer(config,resolver);
	        //consumer.start("PulsarESTopic");

	      
	       // await().atLeast(6, SECONDS).pollDelay(6, SECONDS).until(() -> true);

	        publisher = new PulsarEventPublisher(config.getServiceUrl());
	        publisher.publish("PulsarESTopic2", toBePublished1);
	        publishedEventCount++;
	        publisher.publish("PulsarESTopic2", toBePublished2);
	        publishedEventCount++;
	        publisher.publish("PulsarESTopic2", toBePublished3);
	        publishedEventCount++;
	        System.out.println("pulsar test event published : " + toBePublished1.toString());
	        System.out.println("pulsar test event published : " + toBePublished2.toString());
	        System.out.println("pulsar test event published : " + toBePublished3.toString());
	       
	         pulsarEventStore = new PulsarEventStore(config.getServiceUrl(), "PulsarESTopic2");
	         
	         dehydratedEvents = pulsarEventStore.getById(eventStreamId);
	         
	         Iterator<? extends Event> it = dehydratedEvents.iterator();
	         while(it.hasNext()) {
	        	 
	        	 Event event =it.next();
	        	 dehydratedEventCount++;
	        	 System.out.println("dehydrated event POJO : " + event.toString());
	         }
	         
	       // await().atMost(3, SECONDS).until(() -> publishedEvent != null);
	        
	       
	        
	        
	        
	}

	@AfterEach
	void tearDown() throws Exception {
		publisher.close();
		pulsarEventStore.close();
	}

 	@Test
	void test() {
		
 	    System.out.println("published event count : " + publishedEventCount);
 	    System.out.println("dehydrated event count : " + dehydratedEventCount);
 	    //Assertions.assertEquals(publishedEventCount, dehydratedEventCount);
	} 
 
} */
