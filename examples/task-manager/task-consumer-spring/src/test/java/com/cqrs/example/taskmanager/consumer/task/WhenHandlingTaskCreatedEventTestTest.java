package com.cqrs.example.taskmanager.consumer.task;

import java.time.Clock;
import java.time.Instant;
import java.time.Period;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.cqrs.example.taskmanager.consumer.task.Task;
import com.cqrs.example.taskmanager.events.TaskCreated;
import com.cqrs.messaging.ID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("When handling  task created event")
public class WhenHandlingTaskCreatedEventTestTest extends ProjectionTest<TaskCreated> {
    @Captor
    ArgumentCaptor<Task> argument;

    @Override
    protected void given() {
    }

    @Override
    protected TaskCreated when() {
        return new TaskCreated(ID.fromObject(UUID.randomUUID()), "title", Instant.now(), Instant.now(
            Clock.systemUTC()).plus(Period.ofDays(1)), 1);
    }

    @Test
    @DisplayName("Test task projection is saved")
    void testProjectionIsSaved() {
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Test task projection has same id as event")
    void testProjectionIsSavedWithCorrectId() {
        verify(repository, times(1)).save(argument.capture());
        assertEquals(event.getStreamId().toString(), argument.getValue().getId());
    }

    @Test
    @DisplayName("Test task projection has same title as event")
    void testProjectionIsSavedWithTitle() {
        verify(repository, times(1)).save(argument.capture());
        assertEquals(event.getTitle(), argument.getValue().getTitle());
    }

    @Test
    @DisplayName("Test task projection has same creation date as event")
    void testProjectionIsSavedWithCreateDate() {
        verify(repository, times(1)).save(argument.capture());
        assertEquals(event.getCreationDate(), argument.getValue().getCreationDate());
    }

    @Test
    @DisplayName("Test task projection has same due date as event")
    void testProjectionIsSavedWithDueDate() {
        verify(repository, times(1)).save(argument.capture());
        assertEquals(event.getDueDate(), argument.getValue().getDueDate());
    }
}
