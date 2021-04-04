package com.cqrs.eventbus.kafka;

import static com.cqrs.assertj.Assertions.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventbus.events.TestEvent;
import com.cqrs.eventbus.kafka.KafkaConfig;
import com.cqrs.eventbus.kafka.KafkaEventConsumer;
import com.cqrs.eventbus.kafka.KafkaEventPublisher;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.Deserializer;
import com.cqrs.messaging.EventHandler;
import com.cqrs.messaging.ID;
import com.cqrs.messaging.JsonDeserializer;
import com.cqrs.messaging.JsonSerializer;
import com.cqrs.messaging.Serializer;

@TestInstance(Lifecycle.PER_CLASS)
public class WhenPublishingMultipleEventWithMultipleConsumerTest implements EventHandler<TestEvent> {

    private static Map<ID, TestEvent> toBePublished = new HashMap<>();
    private final KafkaEventConsumer consumer;
    private static Map<ID, TestEvent> publishedEvent = new HashMap<>();

    @Override
    public void handle(TestEvent event) {
        System.out.println(String.format("Handling event Thread ID %s", Thread.currentThread().getId()));
  
        publishedEvent.put(event.getStreamId(), event);
         
    }

    public WhenPublishingMultipleEventWithMultipleConsumerTest() throws InterruptedException {
    	// set configuration to have 3 consumer threads
        KafkaConfig config = new KafkaConfig("localhost:9092", "cqrs-kafka-eventbus-test", 3);
        Deserializer deserializer = new JsonDeserializer();
        Serializer serializer = new JsonSerializer();
        ActionHandlerResolver resolver = new ActionHandlerResolver();
        resolver.registerActionHandler(this);
        consumer = new KafkaEventConsumer(config, serializer, deserializer, resolver);
        consumer.start("TestTopic");
        // First time topic consumer group creation takes bit of time in kafka (~3-4 seconds)
        await().atLeast(3, SECONDS).pollDelay(3, SECONDS).until(() -> true);
        
        for(int i=0; i< 25; i++) {

        ID next = ID.fromObject(UUID.randomUUID());
        toBePublished.put(next, new TestEvent(next, "Awesomeness has happened " +i, i));
        }
      
             
        EventPublisher publisher = new KafkaEventPublisher(config, serializer, deserializer);

        publisher.publish("TestTopic", new ArrayList<>(toBePublished.values()));
        
       
              
        await().atMost(20, SECONDS).until(() -> publishedEvent.size() == 25);
        
        System.out.println("published event map size : " + publishedEvent.size());
        
        System.out.println("published events : " + publishedEvent.toString());
    }

    @Test
    public void testItShouldPublishTheEventToTheRegisteredEventHandler() {
        assertNotNull(publishedEvent, "The published event is not received in the registered handler");
        assertEquals(25, publishedEvent.size(), "The published event is not received in the registered handler");
        for (Map.Entry<ID, TestEvent> toBePublishedEntry : toBePublished.entrySet()) {
            TestEvent toBePublishedTestEvent = toBePublishedEntry.getValue();
            TestEvent publishedTestEvent = publishedEvent.get(toBePublishedEntry.getKey());

            assertThat(publishedTestEvent).isNotNull()
                .hasStreamId(toBePublishedTestEvent.getStreamId())
                .hasEventId()
                .hasVersion(toBePublishedTestEvent.getVersion());

            assertEquals(toBePublishedTestEvent.getStuff(), publishedTestEvent.getStuff(),
                "The published event stuff value has changed");
        }
    
    }

    /*
    @Test
    public void testItShouldHaveTheSameValuesForProperties() {
        for (Map.Entry<ID, TestEvent> toBePublishedEntry : toBePublished.entrySet()) {
            TestEvent toBePublishedTestEvent = toBePublishedEntry.getValue();
            TestEvent publishedTestEvent = publishedEvent.get(toBePublishedEntry.getKey());

            assertThat(publishedTestEvent).isNotNull()
                .hasStreamId(toBePublishedTestEvent.getStreamId())
                .hasEventId()
                .hasVersion(toBePublishedTestEvent.getVersion());

            assertEquals(toBePublishedTestEvent.getStuff(), publishedTestEvent.getStuff(),
                "The published event stuff value has changed");
        }
    } */

    @AfterAll
    private void tearDown() {
        try {
            consumer.close();
        } catch (Exception e) {

        }
    }
}

