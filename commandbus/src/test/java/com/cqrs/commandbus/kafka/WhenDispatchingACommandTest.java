package com.cqrs.commandbus.kafka;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.cqrs.commandbus.commands.TestCommand;
import com.cqrs.commandbus.kafka.KafkaCommandDispatcher;
import com.cqrs.commandbus.kafka.KafkaCommandListener;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.CommandHandler;
import com.cqrs.messaging.Deserializer;
import com.cqrs.messaging.JsonDeserializer;
import com.cqrs.messaging.JsonSerializer;
import com.cqrs.messaging.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestInstance(Lifecycle.PER_CLASS)
public class WhenDispatchingACommandTest implements CommandHandler<TestCommand> {

	private final KafkaCommandDispatcher dispatcher;
	private final KafkaCommandListener listener;
	private final TestCommand toBeDispatched;
	private TestCommand dispatchedCommand;

	@Override
	public void handle(TestCommand command) {
		dispatchedCommand = command;
		
	}

	public WhenDispatchingACommandTest() throws Exception {
		ActionHandlerResolver resolver = new ActionHandlerResolver();
		resolver.registerActionHandler(this);
		Serializer serializer = new JsonSerializer();
		Deserializer deserializer = new JsonDeserializer();

		listener = new KafkaCommandListener("localhost:9092", "cqrs-kafka-commandbus-test", deserializer,
				resolver);
		listener.start();
		await().atLeast(4, SECONDS).pollDelay(4, SECONDS).until(() -> true);
		dispatcher = new KafkaCommandDispatcher("localhost:9092", serializer);

		toBeDispatched = new TestCommand(UUID.randomUUID(), "Awesomeness has happened");

		dispatcher.dispatch(toBeDispatched);

		await().atMost(8, SECONDS).until(() -> dispatchedCommand != null);
	}

	@Test
	public void testItShouldPublishTheEventToTheRegisteredEventHandler() {
		assertNotNull(dispatchedCommand, "The dispatched command is not received in the registered handler");
	}

	@Test
	public void testItShouldHaveTheSameValuesForProperties() {
		assertNotNull(dispatchedCommand);
		assertEquals(dispatchedCommand.getId(), toBeDispatched.getId());
		assertEquals(toBeDispatched.getStuff(), dispatchedCommand.getStuff(),
				"The published command stuff value has changed");
	}

	@AfterAll
	private void tearDown() {
		try {
			listener.stop();
		} catch (Exception e) {

		}
	}

}
