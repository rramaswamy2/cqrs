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

public class WhenCreatingANewTaskWithDueDateInThePastTest extends SingleEventTest<Task, CreateTask, TaskCreated>
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
        return new CreateTask(ID.fromObject(UUID.randomUUID()), "Create CQRS Example", Instant.now().minus(1, DAYS));
    }

    @Test
    public void then() {
        assertThat(getPublishedEvent()).isNull();
        assertThat(getCaughtException()).hasAggregateId(getCommand().getId())
            .hasMessage("Task due date has to be in the future");
    }
}
