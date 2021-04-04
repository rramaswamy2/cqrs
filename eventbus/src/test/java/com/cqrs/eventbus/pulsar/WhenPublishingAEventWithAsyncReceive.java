package com.cqrs.eventbus.pulsar;

import static com.cqrs.assertj.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cqrs.eventbus.events.PulsarTestEvent;
import com.cqrs.messaging.JsonDeserializer;

/* class WhenPublishingAEventWithAsyncReceive {

	private static final String APACHE_PULSAR_AWESOMENESS_HAS_HAPPENED = "Apache Pulsar Awesomeness has happened";
	private static final String TEST_TOPIC = "test_pulsar_topic_2";
	private static final String PULSAR_LOCALHOST_6650 = "pulsar://localhost:6650";
	private static final String PULSAR_LOCALHOST_8080 = "http://localhost:8080";
	private static PulsarTestEvent toBePublished;
	private static PulsarTestEvent published;
	private static PulsarEventConsumer consumer;
	private static PulsarEventPublisher publisher;
	private static JsonDeserializer deser = new JsonDeserializer();

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		toBePublished = new PulsarTestEvent(UUID.randomUUID().toString(), APACHE_PULSAR_AWESOMENESS_HAS_HAPPENED, 1);
		System.out.println("event to publish to pulsar : " + toBePublished.toString());
		consumer = new PulsarEventConsumer(PULSAR_LOCALHOST_6650);
		CompletableFuture<String> futureJson = consumer.startSubscribeToTopicAndAsyncReceive(TEST_TOPIC);
		publisher = new PulsarEventPublisher(PULSAR_LOCALHOST_6650);
		publisher.publish(TEST_TOPIC, toBePublished);
		System.out.println("pulsar test event published : " + toBePublished.toString());
        String jsonStr = futureJson.get();
        System.out.println(" json event received : " + jsonStr);
		published = deser.deserialize(jsonStr, PulsarTestEvent.class);

	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		consumer.close();
		publisher.close();
	}

	@BeforeEach
	void setUp() throws Exception {
	
	}

	@AfterEach
	void tearDown() throws Exception {

	}

	@Test
	public void testItShouldHaveTheSameValuesForProperties() {
		// match all event attributes like eventId, streamId and version
		System.out.println("to be published event : " + toBePublished.toString());
		System.out.println("published event : " + published.toString());

		assertThat(published).isNotNull().hasStreamId(toBePublished.getStreamId())
				.hasEventId(toBePublished.getEventId()).hasVersion(toBePublished.getVersion());
		assertEquals(toBePublished.getEventDescription(), published.getEventDescription(),
				"The published event description value has changed");
	}
} */
