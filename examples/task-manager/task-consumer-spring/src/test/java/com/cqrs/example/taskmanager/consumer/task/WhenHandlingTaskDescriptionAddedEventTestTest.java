package com.cqrs.example.taskmanager.consumer.task;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import com.cqrs.example.taskmanager.consumer.task.Task;
import com.cqrs.example.taskmanager.events.TaskDescriptionAdded;
import com.cqrs.messaging.ID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("When handling  task description added event")
public class WhenHandlingTaskDescriptionAddedEventTestTest extends ProjectionTest<TaskDescriptionAdded> {
    @Captor
    ArgumentCaptor<Task> argument;

    private ID taksId = ID.fromObject(UUID.randomUUID());

    private Task initialTask;

    private Instant instant = Instant.now();

    @Override
    protected void given() {
        initialTask = new Task(taksId.toString(), "title", "Description", instant, instant, instant);
        org.mockito.Mockito.when(repository.findById(taksId.toString())).thenReturn(Optional.of(initialTask));
    }


    @Override
    protected TaskDescriptionAdded when() {
        return new TaskDescriptionAdded(taksId, "Description", 2);
    }

    @Test
    @DisplayName("Test task projection is saved")
    void testProjectionIsSaved() {
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Test task projection has same description as event")
    void testProjectionIsSavedWithSameDescription() {
        verify(repository, times(1)).save(argument.capture());
        assertEquals(event.getDescription(), argument.getValue().getDescription());
    }

}
