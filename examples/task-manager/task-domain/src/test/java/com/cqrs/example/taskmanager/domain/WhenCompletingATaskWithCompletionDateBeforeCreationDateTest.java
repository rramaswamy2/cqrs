package com.cqrs.example.taskmanager.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cqrs.domain.AggregateRepository;
import com.cqrs.domain.Repository;
import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventstore.EventStore;
import com.cqrs.example.taskmanager.commands.CompleteTask;
import com.cqrs.example.taskmanager.domain.CompleteTaskCommandHandler;
import com.cqrs.example.taskmanager.domain.Task;
import com.cqrs.example.taskmanager.domain.TaskException;
import com.cqrs.example.taskmanager.events.TaskCompleted;
import com.cqrs.example.taskmanager.events.TaskCreated;
import com.cqrs.junit.SingleEventTest;
import com.cqrs.messaging.CommandHandler;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.EventHandler;
import com.cqrs.messaging.ID;

import static com.cqrs.assertj.Assertions.assertThat;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

public class WhenCompletingATaskWithCompletionDateBeforeCreationDateTest
    extends SingleEventTest<Task, CompleteTask, TaskCompleted>
    implements EventHandler<TaskCompleted> {

    private static ID taskId = ID.fromObject(UUID.randomUUID());

    @Override
    protected CommandHandler commandHandlerFactory(Repository<Task> repository) {
        return new CompleteTaskCommandHandler(repository);
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
            add(new TaskCreated(taskId, "This is really amazing stuff",
                Instant.now(),
                Instant.now().plus(31, DAYS), 1));
        }};
        eventStore.save(Task.class.getSimpleName(), taskId.toString(), domainEvents, 0);
    }

    @Override
    protected CompleteTask when() {
        return new CompleteTask(taskId, Instant.now().minus(1, HOURS));
    }

    @Test
    public void then() {
        assertThat(getPublishedEvent()).isNull();

        assertThat(getCaughtException()).isInstanceOf(TaskException.class)
            .hasAggregateId(getCommand().getId())
            .hasMessage("Completion date must be after the creation date");
    }
}
