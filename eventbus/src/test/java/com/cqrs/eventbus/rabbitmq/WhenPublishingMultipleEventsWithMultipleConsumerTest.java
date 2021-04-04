package com.cqrs.eventbus.rabbitmq;

import static com.cqrs.assertj.Assertions.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventbus.events.TestEvent;
import com.cqrs.eventbus.events.TestRabbitEvent;
import com.cqrs.eventbus.rabbitmq.RabbitMqConfig;
import com.cqrs.eventbus.rabbitmq.RabbitmqEventConsumer;
import com.cqrs.eventbus.rabbitmq.RabbitmqEventPublisher;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.Deserializer;
import com.cqrs.messaging.EventHandler;
import com.cqrs.messaging.ID;
import com.cqrs.messaging.JsonDeserializer;
import com.cqrs.messaging.JsonSerializer;
import com.cqrs.messaging.Serializer;

public class WhenPublishingMultipleEventsWithMultipleConsumerTest {

    private final Map<ID,TestEvent> toBePublishedEventMap = new HashMap<>();
    private final Map<ID, TestRabbitEvent> toBePublishedRabbitEventMap = new HashMap<>();
    private static Map<ID, TestEvent> publishedEventMap = new HashMap<>();
    private static Map<ID, TestRabbitEvent> publishedRabbitEventMap = new HashMap<>();
    //private TestEvent publishedEvent;
    private int count;

    private EventHandler<TestEvent> testEventHandler = new EventHandler<TestEvent>() {
    @Override
    public void handle(TestEvent event) {
      
    	  publishedEventMap.put(event.getStreamId(), event);
    	count++;
    }
    };
    
    private EventHandler<TestRabbitEvent> testRabbitEventHandler = new EventHandler<TestRabbitEvent>() {
    @Override
    public void handle(TestRabbitEvent event) {
    	
    	publishedRabbitEventMap.put(event.getStreamId(), event);
    	count++;
    }
    };

    public WhenPublishingMultipleEventsWithMultipleConsumerTest() {
        Deserializer deserializer = new JsonDeserializer();
        Serializer serializer = new JsonSerializer();

        ActionHandlerResolver resolver1 = new ActionHandlerResolver();
        resolver1.registerActionHandler(testEventHandler);
        
        ActionHandlerResolver resolver2 = new ActionHandlerResolver();
        resolver2.registerActionHandler(testRabbitEventHandler);

        RabbitMqConfig config = new RabbitMqConfig("localhost", 5672);
        
        // creating 2 instances of consumer
        //consumer1 to subscribe and bind to regular events 
        RabbitmqEventConsumer consumer = new RabbitmqEventConsumer(config, deserializer, serializer, resolver1);
        consumer.start("my-test-stream");
        
        //consumer2 to subscribe and bind to rabbitmq specific events  
        RabbitmqEventConsumer consumer2 = new RabbitmqEventConsumer(config, deserializer, serializer, resolver2);
        consumer2.start("my-test-stream");
        
        
        
        for(int i=0; i< 10; i++) {

            ID next = ID.fromObject(UUID.randomUUID());
            toBePublishedEventMap.put(next, new TestEvent(next, "Event Awesomeness has happened " +i, i));
            }
        
        for(int i=0; i< 10; i++) {

            ID next = ID.fromObject(UUID.randomUUID());
            toBePublishedRabbitEventMap.put(next, new TestRabbitEvent(next, "RabbitMQ Event Awesomeness has happened " +i, i));
            }
        
        

        //toBePublished = new TestEvent(ID.fromObject(UUID.randomUUID()), "Awesomeness has happened", 1);
        EventPublisher publisher = new RabbitmqEventPublisher(config, serializer, deserializer);
        publisher.publish("my-test-stream", toBePublishedEventMap.values());
        
        publisher.publish("my-test-stream", toBePublishedRabbitEventMap.values());
        await().atMost(20, SECONDS).until(() -> count ==20);
    }

    @Test
    public void testItShouldPublishTheEventToTheRegisteredEventHandler() {
    	
    	 assertEquals(10, publishedEventMap.size(), "The published event is not received in the registered handler");
         assertEquals(10, publishedRabbitEventMap.size(), "The published rabbitMQ event is not received in the registered handler");
    	   	
    	
    }

    @Test
    public void testItShouldHaveTheSameValuesForProperties() {
    	   for (Map.Entry<ID, TestEvent> toBePublishedEntry : toBePublishedEventMap.entrySet()) {
               TestEvent toBePublishedTestEvent = toBePublishedEntry.getValue();
               TestEvent publishedTestEvent = publishedEventMap.get(toBePublishedEntry.getKey());

               assertThat(publishedTestEvent).isNotNull()
                   .hasStreamId(toBePublishedTestEvent.getStreamId())
                   .hasEventId()
                   .hasVersion(toBePublishedTestEvent.getVersion());

               assertEquals(toBePublishedTestEvent.getStuff(), publishedTestEvent.getStuff(),
                   "The published event stuff value has changed");
           }
    	   
    	   for (Map.Entry<ID, TestRabbitEvent> toBePublishedEntry : toBePublishedRabbitEventMap.entrySet()) {
               TestRabbitEvent toBePublishedTestEvent = toBePublishedEntry.getValue();
               TestRabbitEvent publishedTestEvent = publishedRabbitEventMap.get(toBePublishedEntry.getKey());

               assertThat(publishedTestEvent).isNotNull()
                   .hasStreamId(toBePublishedTestEvent.getStreamId())
                   .hasEventId()
                   .hasVersion(toBePublishedTestEvent.getVersion());

               assertEquals(toBePublishedTestEvent.getStuff(), publishedTestEvent.getStuff(),
                   "The published rabbitMQ event stuff value has changed");
           }  
    }
}
