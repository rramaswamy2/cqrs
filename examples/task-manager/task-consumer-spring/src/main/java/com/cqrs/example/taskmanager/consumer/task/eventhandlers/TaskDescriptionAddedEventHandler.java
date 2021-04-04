package com.cqrs.example.taskmanager.consumer.task.eventhandlers;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cqrs.example.taskmanager.consumer.task.Task;
import com.cqrs.example.taskmanager.consumer.task.TaskRepository;
import com.cqrs.example.taskmanager.events.TaskDescriptionAdded;
import com.cqrs.messaging.EventHandler;

public class TaskDescriptionAddedEventHandler implements EventHandler<TaskDescriptionAdded> {
    private static final Logger LOG = LogManager.getLogger(TaskDescriptionAddedEventHandler.class);

    private final TaskRepository repository;

    public TaskDescriptionAddedEventHandler(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(TaskDescriptionAdded event) {
        LOG.debug(() -> String.format("Received event: %s", event));
        Optional<Task> taskOptional = repository.findById(event.getStreamId().toString());
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setDescription(event.getDescription());
            repository.save(task);
        }
    }
}
