package com.cqrs.eventbus.pulsar;

import static com.cqrs.assertj.Assertions.assertThat;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
public class WhenPublishingAEventTest implements EventHandler<PulsarTestEvent> {

    private static final String PULSAR_LOCALHOST_6650 = "pulsar://localhost:6650";
    private static final String PULSAR_LOCALHOST_8080 = "http://localhost:8080";
    private final PulsarTestEvent toBePublished;
    private final PulsarEventConsumer consumer;
    private final PulsarEventPublisher publisher;
    private PulsarTestEvent publishedEvent;

    @Override
    public void handle(PulsarTestEvent event) {
        System.out.println(String.format("Received below pulsar event at event handler by Thread %s \n %s", Thread.currentThread().getName(), event.toString()));
        publishedEvent = event;
    }

    public WhenPublishingAEventTest() throws PulsarClientException {
       
        PulsarConfig config = new PulsarConfig(PULSAR_LOCALHOST_6650,"shared");
    	   	
        ActionHandlerResolver resolver = new ActionHandlerResolver();
        resolver.registerActionHandler(this);
        toBePublished = new PulsarTestEvent(UUID.randomUUID().toString(), "Apache Pulsar Awesomeness has happened", 1);

        System.out.println("event to publish to pulsar : " + toBePublished.toString());
        
        consumer = new PulsarEventConsumer(config,resolver);
        consumer.start("StaticTestTopic");

      
       // await().atLeast(6, SECONDS).pollDelay(6, SECONDS).until(() -> true);

        publisher = new PulsarEventPublisher(config.getServiceUrl());
        publisher.publish("StaticTestTopic", toBePublished);
        System.out.println("pulsar test event published : " + toBePublished.toString());
        await().atMost(3, SECONDS).until(() -> publishedEvent != null);
    }

    @Test
    public void testItShouldPublishTheEventToTheRegisteredEventHandler() {
        assertNotNull(publishedEvent, "The published event is not received in the registered handler");
    }

    @Test
    public void testItShouldHaveTheSameValuesForProperties() {
       // match all event attributes like eventId, streamId and version
    	assertThat(publishedEvent).isNotNull()
            .hasStreamId(toBePublished.getStreamId())
            .hasEventId(toBePublished.getEventId())
            .hasVersion(toBePublished.getVersion());
        assertEquals(toBePublished.getEventDescription(), publishedEvent.getEventDescription(), "The published event description value has changed");
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
