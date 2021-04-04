package com.cqrs.example.taskmanager.consumer.task;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.cqrs.example.taskmanager.consumer.task.Task;
import com.cqrs.example.taskmanager.events.TaskCompleted;
import com.cqrs.messaging.ID;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("When handling task completed event")
public class WhenHandlingTaskCompletedEventTestTest extends ProjectionTest<TaskCompleted> {
    @Captor
    ArgumentCaptor<Task> argument;

    private ID taksId = ID.fromObject(UUID.randomUUID());

    private Task initialTask;

    private Instant instant = Instant.now();

    @Override
    protected void given() {
        initialTask = new Task(taksId.toString(), "title", "Description", instant, instant.plus(2, DAYS), instant);
        org.mockito.Mockito.when(repository.findById(taksId.toString())).thenReturn(Optional.of(initialTask));
    }

    @Override
    protected TaskCompleted when() {
        return new TaskCompleted(taksId, instant, 1);
    }

    @Test
    @DisplayName("Test task projection is saved")
    void testProjectionIsSaved() {
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Test task projection has same completed date as event")
    void testProjectionIsSavedWithSameCompletedDate() {
        verify(repository, times(1)).save(argument.capture());
        assertEquals(event.getCompletedAt(), argument.getValue().getCompletedAt());
    }

}
