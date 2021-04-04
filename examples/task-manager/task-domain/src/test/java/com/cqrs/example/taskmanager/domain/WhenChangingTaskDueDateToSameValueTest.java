package com.cqrs.example.taskmanager.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.cqrs.domain.AggregateRepository;
import com.cqrs.domain.Repository;
import com.cqrs.eventbus.EventPublisher;
import com.cqrs.eventstore.EventStore;
import com.cqrs.example.taskmanager.commands.ChangeTaskDueDate;
import com.cqrs.example.taskmanager.domain.ChangeTaskDueDateCommandHandler;
import com.cqrs.example.taskmanager.domain.Task;
import com.cqrs.example.taskmanager.domain.TaskNothingChangedException;
import com.cqrs.example.taskmanager.events.TaskCreated;
import com.cqrs.example.taskmanager.events.TaskDueDateChanged;
import com.cqrs.junit.SingleEventTest;
import com.cqrs.messaging.CommandHandler;
import com.cqrs.messaging.Event;
import com.cqrs.messaging.EventHandler;
import com.cqrs.messaging.ID;

import static com.cqrs.assertj.Assertions.assertThat;
import static java.time.temporal.ChronoUnit.DAYS;

public class WhenChangingTaskDueDateToSameValueTest extends SingleEventTest<Task, ChangeTaskDueDate, TaskDueDateChanged>
    implements EventHandler<TaskDueDateChanged> {

    private static ID taskId = ID.fromObject(UUID.randomUUID());
    private static Instant dueDate = Instant.now().plus(5, DAYS);

    @Override
    protected CommandHandler commandHandlerFactory(Repository<Task> repository) {
        return new ChangeTaskDueDateCommandHandler(repository);
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
            add(new TaskCreated(taskId, "This is really amazing stuff", Instant.now(), dueDate, 1));
        }};
        eventStore.save(Task.class.getSimpleName(), taskId.toString(), domainEvents, 0);
    }

    @Override
    protected ChangeTaskDueDate when() {
        return new ChangeTaskDueDate(taskId, dueDate);
    }

    @Test
    public void then() {
        assertThat(getPublishedEvent()).isNull();

        assertThat(getCaughtException()).isInstanceOf(TaskNothingChangedException.class)
            .hasAggregateId(getCommand().getId())
            .hasMessage(String.format("Task due date didn't change compared to current value %s", dueDate));
    }
}
