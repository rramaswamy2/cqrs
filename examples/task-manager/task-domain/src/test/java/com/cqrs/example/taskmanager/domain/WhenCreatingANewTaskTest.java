package com.cqrs.example.taskmanager.domain;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cqrs.domain.AggregateRepository;
import com.cqrs.domain.Repository;
import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventstore.EventStore;
import com.cqrs.example.taskmanager.commands.CreateTask;
import com.cqrs.example.taskmanager.domain.CreateTaskCommandHandler;
import com.cqrs.example.taskmanager.domain.Task;
import com.cqrs.example.taskmanager.events.TaskCreated;
import com.cqrs.junit.SingleEventTest;
import com.cqrs.messaging.CommandHandler;
import com.cqrs.messaging.EventHandler;
import com.cqrs.messaging.ID;

import static com.cqrs.assertj.Assertions.assertThat;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WhenCreatingANewTaskTest extends SingleEventTest<Task, CreateTask, TaskCreated>
    implements EventHandler<TaskCreated> {

    @Override
    protected CommandHandler commandHandlerFactory(Repository<Task> repository) {
        return new CreateTaskCommandHandler(repository);
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

    }

    @Override
    protected CreateTask when() {
        return new CreateTask(ID.fromObject(UUID.randomUUID()), "Create CQRS Example", Instant.now().plus(5, DAYS));
    }

    @Test
    public void then() {
        assertThat(getCaughtException()).isNull();
        assertThat(getPublishedEvent()).hasStreamId(getCommand().getId()).hasEventId().hasVersion(1);
        assertEquals(getCommand().getTitle(), getPublishedEvent().getTitle(), "Expected different title");
        org.assertj.core.api.Assertions.assertThat(getPublishedEvent().getCreationDate())
            .isCloseTo(Instant.now(), within(1, SECONDS));
        assertEquals(getCommand().getDueDate(), getPublishedEvent().getDueDate(), "Expected different due date");
    }
}
