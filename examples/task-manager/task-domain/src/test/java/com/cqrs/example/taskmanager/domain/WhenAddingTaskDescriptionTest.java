package com.cqrs.example.taskmanager.domain;

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
import com.cqrs.example.taskmanager.events.TaskCreated;
import com.cqrs.example.taskmanager.events.TaskDescriptionAdded;
import com.cqrs.junit.SingleEventTest;
import com.cqrs.messaging.CommandHandler;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.EventHandler;
import com.cqrs.messaging.ID;

import static com.cqrs.assertj.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhenAddingTaskDescriptionTest extends SingleEventTest<Task, AddDescriptionToTask, TaskDescriptionAdded>
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
        }};
        eventStore.save(Task.class.getSimpleName(), taskId.toString(), domainEvents, 0);
    }

    @Override
    protected AddDescriptionToTask when() {
        return new AddDescriptionToTask(taskId, "Test description");
    }

    @Test
    public void then() {
        assertThat(getCaughtException()).isNull();
        assertThat(getPublishedEvent()).hasStreamId(getCommand().getId()).hasEventId().hasVersion(2);
        assertEquals(getCommand().getDescription(), getPublishedEvent().getDescription(), "Expected given description");
    }
}
