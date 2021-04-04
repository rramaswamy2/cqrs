package com.cqrs.example.taskmanager.domain;

import static com.cqrs.assertj.Assertions.assertThat;

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cqrs.domain.AggregateRepository;
import com.cqrs.domain.Repository;
import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventstore.EventStore;
import com.cqrs.example.taskmanager.commands.AddDescriptionToTask;
import com.cqrs.example.taskmanager.domain.AddDescriptionToTaskCommandHandler;
import com.cqrs.example.taskmanager.domain.Task;
import com.cqrs.example.taskmanager.domain.TaskNothingChangedException;
import com.cqrs.example.taskmanager.events.TaskCreated;
import com.cqrs.example.taskmanager.events.TaskDescriptionAdded;
import com.cqrs.junit.SingleEventTest;
import com.cqrs.messaging.CommandHandler;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.EventHandler;
import com.cqrs.messaging.ID;

public class WhenAddingTheSameTaskDescriptionTest
    extends SingleEventTest<Task, AddDescriptionToTask, TaskDescriptionAdded>
    implements EventHandler<TaskDescriptionAdded> {

    private static ID taskId = ID.fromObject(UUID.randomUUID());

    @Override
    protected CommandHandler commandHandlerFactory(Repository<Task> repository) {
        return new AddDescriptionToTaskCommandHandler(repository);
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
            add(new TaskCreated(taskId, "This is really amazing stuff", Instant.now(),
                Instant.now().plus(Period.ofDays(1)), 1));
            add(new TaskDescriptionAdded(taskId, "Duplicate test description", 2));
        }};
        eventStore.save(Task.class.getSimpleName(), taskId.toString(), domainEvents, 0);
    }

    @Override
    protected AddDescriptionToTask when() {
        return new AddDescriptionToTask(taskId, "Duplicate test description");
    }

    @Test
    public void then() {
        assertThat(getPublishedEvent()).isNull();

        assertThat(getCaughtException()).isInstanceOf(TaskNothingChangedException.class)
            .hasAggregateId(getCommand().getId())
            .hasMessage("Task description didn't change compared to current value Duplicate test description");
    }
}
