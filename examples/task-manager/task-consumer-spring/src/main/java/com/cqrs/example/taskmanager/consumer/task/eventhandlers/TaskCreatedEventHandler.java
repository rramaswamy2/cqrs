package com.cqrs.example.taskmanager.consumer.task.eventhandlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cqrs.example.taskmanager.consumer.task.Task;
import com.cqrs.example.taskmanager.consumer.task.TaskRepository;
import com.cqrs.example.taskmanager.events.TaskCreated;
import com.cqrs.messaging.EventHandler;

public class TaskCreatedEventHandler implements EventHandler<TaskCreated> {

    private static final Logger LOG = LogManager.getLogger(TaskCreatedEventHandler.class);

    private final TaskRepository repository;

    public TaskCreatedEventHandler(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(TaskCreated event) {
        LOG.debug(() -> String.format("Received event: %s", event));
        Task task = new Task(event.getStreamId().toString(), event.getTitle(), null, event.getDueDate(), null, event.getCreationDate());
        this.repository.save(task);
    }
}
