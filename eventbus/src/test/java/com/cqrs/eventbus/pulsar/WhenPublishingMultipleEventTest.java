package com.cqrs.eventbus.pulsar;

import static com.cqrs.assertj.Assertions.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.cqrs.eventbus.events.PulsarTestEvent;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.EventHandler;

/* @TestInstance(Lifecycle.PER_CLASS)
public class WhenPublishingMultipleEventTest implements EventHandler<PulsarTestEvent> {

    private static final String PULSAR_LOCALHOST_6650 = "pulsar://localhost:6650";
    private static final String PULSAR_LOCALHOST_8080 = "http://localhost:8080";
    private static Map<String, PulsarTestEvent> toBePublishedMap = new HashMap<>();
    private final PulsarEventPublisher publisher;
    private final PulsarEventConsumer consumer;
    private static Map<String, PulsarTestEvent> publishedEventMap = new HashMap<>();

    @Override
    public void handle(PulsarTestEvent event) {
        System.out.println(String.format("Handling event Thread %s", Thread.currentThread().getName()));
        publishedEventMap.put(event.getStreamId().toString(), event);
    }

    public WhenPublishingMultipleEventTest() throws PulsarClientException {
        
        PulsarConfig config = new PulsarConfig(PULSAR_LOCALHOST_6650,"shared");
    	
        ActionHandlerResolver resolver = new ActionHandlerResolver();
        resolver.registerActionHandler(this);

        String idOne = UUID.randomUUID().toString();
        toBePublishedMap.put(idOne, new PulsarTestEvent(idOne, "Pulsar Awesomeness has happened 1", 1));

        String idTwo = UUID.randomUUID().toString();
        toBePublishedMap.put(idTwo, new PulsarTestEvent(idTwo, "Pulsar Awesomeness has happened 2", 2));

        String idThree = UUID.randomUUID().toString();
        toBePublishedMap.put(idThree, new PulsarTestEvent(idThree, "Pulsar Awesomeness has happened 3", 3));
        consumer = new PulsarEventConsumer(config, resolver);
        consumer.start("TestEvent");
        
        //await().atLeast(6, SECONDS).pollDelay(6, SECONDS).until(() -> true);
        publisher = new PulsarEventPublisher(config.getServiceUrl());

        publisher.publish("TestEvent", new ArrayList<>(toBePublishedMap.values()));
        await().atMost(15, SECONDS).until(() -> publishedEventMap.size() == 3);
    }

    @Test
    public void testItShouldPublishTheEventToTheRegisteredEventHandler() {
        assertNotNull(publishedEventMap, "The published event is not received in the registered handler");
        System.out.println("to be published map to pulsar message broker below  : \n " + toBePublishedMap.toString());
        System.out.println("published event map consumed at event handler below : \n" + publishedEventMap.toString());
        assertEquals(3, publishedEventMap.size(), "The published event is not received in the registered handler");
    }

    @Test
    public void testItShouldHaveTheSameValuesForProperties() {
        for (Map.Entry<String, PulsarTestEvent> toBePublishedEntry : toBePublishedMap.entrySet()) {
            PulsarTestEvent toBePublished = toBePublishedEntry.getValue();
            PulsarTestEvent publishedEvent = publishedEventMap.get(toBePublishedEntry.getKey());

            assertThat(publishedEvent).isNotNull()
                .hasStreamId(toBePublished.getStreamId())
                .hasEventId(toBePublished.getEventId())
                .hasVersion(toBePublished.getVersion());

            assertEquals(toBePublished.getEventDescription(), publishedEvent.getEventDescription(),
                "The published event description value has changed");
        }
    }

    @AfterAll
    private void tearDown() {
        try {
            publisher.close();
        	consumer.close();
        } catch (Exception e) {

        }
    }
} */
