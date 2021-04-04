package com.cqrs.example.taskmanager.domain;

import static com.cqrs.assertj.Assertions.assertThat;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.cqrs.commandbus.kafka.KafkaCommandDispatcher;
import com.cqrs.commandbus.kafka.KafkaCommandListener;
import com.cqrs.domain.AggregateRepository;
import com.cqrs.domain.Repository;
import com.cqrs.eventbus.local.SimpleEventPublisher;
import com.cqrs.eventstore.EventStore;
import com.cqrs.eventstore.local.InMemoryEventStore;
import com.cqrs.example.taskmanager.commands.ChangeTaskStatus;
import com.cqrs.example.taskmanager.commands.CreateTask;
import com.cqrs.example.taskmanager.domain.Task;
import com.cqrs.example.taskmanager.events.TaskCreated;
import com.cqrs.example.taskmanager.events.TaskStatusChanged;
import com.cqrs.messaging.ActionHandlerResolver;
import com.cqrs.messaging.CommandHandler;
import com.cqrs.messaging.Deserializer;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.EventHandler;
import com.cqrs.messaging.ID;
import com.cqrs.messaging.JsonDeserializer;
import com.cqrs.messaging.JsonSerializer;
import com.cqrs.messaging.Serializer;

public class TestKafkaCommandDispatcher {

	private static ID taskId = ID.fromObject(UUID.randomUUID());
	private static KafkaCommandDispatcher dispatcher;
	private static KafkaCommandListener listener1;
	private static CreateTask toBeDispatched1;
	private static ChangeTaskStatus toBeDispatched2;
	private static CreateTask dispatchedCommand1;
	private static ChangeTaskStatus dispatchedCommand2;
	private static TaskCreated publishedEvent1;
	private static TaskStatusChanged publishedEvent2;

	@SuppressWarnings("rawtypes")
	private static HashMap<String, List> eventMap = new HashMap<String, List>();
	private static Repository<Task> repository;
	private static Task updatedTask1;
	private static Task updatedTask2;

	private static class CreateTaskCommandHandler implements CommandHandler<CreateTask> {

		private final Repository<Task> aggrRepository;

		public CreateTaskCommandHandler(Repository<Task> repository) {
			this.aggrRepository = repository;
		}

		@Override
		public void handle(CreateTask command) {

			dispatchedCommand1 = command;
			Task task = new Task(command.getId(), command.getTitle(), command.getDueDate());

			this.aggrRepository.save(task, 0);

			updatedTask1 = aggrRepository.getById(command.getId());

			System.out.println("updated task with CreateTask command : " + updatedTask1.toString());

		}
	}

	private static class ChangeTaskStatusCommandHandler implements CommandHandler<ChangeTaskStatus> {

		private final Repository<Task> aggrRepository;

		public ChangeTaskStatusCommandHandler(Repository<Task> repository) {
			this.aggrRepository = repository;
		}

		@Override
		public void handle(ChangeTaskStatus command) {
			dispatchedCommand2 = command;

			System.out.println("dispatched ChangeTaskStatus command : " + command.toString());
			Task task = aggrRepository.getById(command.getId());
			task.changeStatus(command.getStatus());

			aggrRepository.save(task, task.getVersion());

			updatedTask2 = aggrRepository.getById(command.getId());

			System.out.println("updated task with ChangeTaskStatus command : " + updatedTask2.toString());

		}

	}

	private static class TaskCreatedEventHandler implements EventHandler<TaskCreated> {

		@SuppressWarnings("unchecked")
		@Override
		public void handle(TaskCreated event) {
			publishedEvent1 = event;
			if (eventMap.containsKey(event.getStreamId().toString())) {
				List eventList = eventMap.get(event.getStreamId().toString());
				eventList.add(event);
				eventMap.put(event.getStreamId().toString(), eventList);

			} else {

				List eventList = new ArrayList();
				eventList.add(event);
				eventMap.put(event.getStreamId().toString(), eventList);
			}
		}
	}

	private static class TaskStatusChangedEventHandler implements EventHandler<TaskStatusChanged> {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void handle(TaskStatusChanged event) {

			publishedEvent2 = event;
			if (eventMap.containsKey(event.getStreamId().toString())) {
				List eventList = eventMap.get(event.getStreamId().toString());
				eventList.add(event);
				eventMap.put(event.getStreamId().toString(), eventList);

			} else {

				List eventList = new ArrayList();
				eventList.add(event);
				eventMap.put(event.getStreamId().toString(), eventList);
			}
		}

	}

	@BeforeAll
	static void TestKafkaCommandDispatcher() throws Exception {

		ActionHandlerResolver eventResolver = new ActionHandlerResolver();
		eventResolver.registerActionHandler(new TaskCreatedEventHandler());
		eventResolver.registerActionHandler(new TaskStatusChangedEventHandler());
		EventStore es = new InMemoryEventStore();
		SimpleEventPublisher sep = new SimpleEventPublisher(eventResolver);
		repository = new AggregateRepository<Task>(es, sep) {
			@Override
			public void save(Task aggregate, int version) {
				super.save(aggregate, version);
			}
		};

		ActionHandlerResolver commandResolver = new ActionHandlerResolver();
		commandResolver.registerActionHandler(new ChangeTaskStatusCommandHandler(repository));
		commandResolver.registerActionHandler(new CreateTaskCommandHandler(repository));
		Serializer serializer = new JsonSerializer();
		Deserializer deserializer = new JsonDeserializer();

		listener1 = new KafkaCommandListener("localhost:9092", "cqrs-kafka-commandbus-test", deserializer,
				commandResolver);
		listener1.start();
		await().atLeast(6, SECONDS).pollDelay(6, SECONDS).until(() -> true);

		toBeDispatched1 = new CreateTask(taskId, "Create CQRS Task example", Instant.now().plus(5, DAYS));

		toBeDispatched2 = new ChangeTaskStatus(taskId, "Task In-progress");

		dispatcher = new KafkaCommandDispatcher("localhost:9092", serializer);

		dispatcher.dispatch(toBeDispatched1);

		await().atMost(10, SECONDS).until(() -> dispatchedCommand1 != null);

		await().atMost(5, SECONDS).until(() -> updatedTask1 != null); // wait until first task creation event gets
																		// persisted into event store to avoid aggregate
																		// not found exception

		dispatcher.dispatch(toBeDispatched2);

		await().atMost(10, SECONDS).until(() -> dispatchedCommand2 != null);

		await().atMost(5, SECONDS).until((() -> publishedEvent1 != null));

		await().atMost(5, SECONDS).until((() -> publishedEvent2 != null));

		Iterable<? extends Event> events = es.getById(taskId.toString());

		for (Event event : events) {

			System.out.println("domain events : " + event.toString());
		}

	}

	@AfterAll
	private static void tearDown() {
		try {
			listener1.stop();
		} catch (Exception e) {

		}
	}

	@Test
	public void testItShouldDispatchTheCommandToTheRegisteredCommandHandler() {
		assertEquals(dispatchedCommand1, toBeDispatched1,
				"The dispatched command is not received in the registered handler");
	}

	@Test
	public void testItShouldHaveTheSameValuesForProperties() {
		assertEquals(dispatchedCommand1.getId(), toBeDispatched1.getId(),
				"The dispatched command id value has changed");
		assertEquals(dispatchedCommand1.getTitle(), toBeDispatched1.getTitle(),
				"The dispatched command stuff value has changed");
	}

	@Test
	public void testSecondCommandShouldDispatchTheCommandToTheRegisteredCommandHandler() {
		assertEquals(dispatchedCommand2, toBeDispatched2,
				"The dispatched command is not received in the registered handler");
	}

	@Test
	public void testSecondCommandShouldHaveTheSameValuesForProperties() {
		assertEquals(dispatchedCommand2.getId(), toBeDispatched2.getId(),
				"The dispatched command id value has changed");
		assertEquals(dispatchedCommand2.getStatus(), toBeDispatched2.getStatus(),
				"The dispatched command stuff value has changed");
	}

	@Test
	void testEndToEndFromCommandDispatchToEventHandling() {
		System.out.println("published event map : " + eventMap.toString());
		assertEquals(eventMap.get(taskId.toString()).size(), 2);

		assertThat(publishedEvent1).hasStreamId(toBeDispatched1.getId()).hasEventId().hasVersion(1);
		assertEquals(publishedEvent1.getTitle(), toBeDispatched1.getTitle(), "Expected given title");
		assertEquals(publishedEvent1.getDueDate(), toBeDispatched1.getDueDate(), "Expected different due date");

		assertThat(publishedEvent2).hasStreamId(toBeDispatched2.getId()).hasEventId().hasVersion(2);
		assertEquals(toBeDispatched2.getStatus(), publishedEvent2.getStatus(), "Expected given status");

	}

	@Test
	void testAggregateDehydratedFromEventHistory() {

		assertEquals(updatedTask2.getStatus(), toBeDispatched2.getStatus());
		System.out.println("restored task from event history : " + updatedTask2.toString());
	}

}
