package com.cqrs.example.taskmanager.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cqrs.domain.AggregateRepository;
import com.cqrs.domain.Repository;
import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventstore.EventStore;
import com.cqrs.example.taskmanager.commands.ChangeTaskTitle;
import com.cqrs.example.taskmanager.domain.ChangeTaskTitleCommandHandler;
import com.cqrs.example.taskmanager.domain.Task;
import com.cqrs.example.taskmanager.domain.TaskException;
import com.cqrs.example.taskmanager.events.TaskCreated;
import com.cqrs.example.taskmanager.events.TaskTitleChanged;
import com.cqrs.junit.SingleEventTest;
import com.cqrs.messaging.CommandHandler;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.EventHandler;
import com.cqrs.messaging.ID;

import static com.cqrs.assertj.Assertions.assertThat;
import static java.time.temporal.ChronoUnit.DAYS;

public class WhenErasingTaskTitleTest extends SingleEventTest<Task, ChangeTaskTitle, TaskTitleChanged>
    implements EventHandler<TaskTitleChanged> {

    private static ID taskId = ID.fromObject(UUID.randomUUID());

    @Override
    protected CommandHandler commandHandlerFactory(Repository<Task> repository) {
        return new ChangeTaskTitleCommandHandler(repository);
    }

    @Override
    protected Repository<Task> repositoryFactory(EventStore eventStore, EventPublisher publisher) {
        return new AggregateRepository<Task>(eventStore, publisher) {
            @Override
            public void save(Task aggregate, int version) {
                super.save(aggregate, version);
            }
        };
    }

    @Override
    protected void given(EventStore eventStore) {
        Iterable<Event> domainEvents = new ArrayList<Event>() {{
            add(new TaskCreated(taskId, "This is amazing shizzle", Instant.now(),
                Instant.now().plus(31, DAYS), 1));
        }};
        eventStore.save(Task.class.getSimpleName(), taskId.toString(), domainEvents, 0);
    }

    @Override
    protected ChangeTaskTitle when() {
        return new ChangeTaskTitle(taskId, "");
    }

    @Test
    public void then() {
        assertThat(getPublishedEvent()).isNull();
        assertThat(getCaughtException()).isInstanceOf(TaskException.class)
            .hasAggregateId(getCommand().getId())
            .hasMessage("Task title cannot be empty");
    }
}
