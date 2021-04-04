package com.cqrs.example.taskmanager.consumer.task.eventhandlers;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cqrs.example.taskmanager.consumer.task.Task;
import com.cqrs.example.taskmanager.consumer.task.TaskRepository;
import com.cqrs.example.taskmanager.events.TaskTitleChanged;
import com.cqrs.messaging.EventHandler;

public class TaskTitleChangedEventHandler implements EventHandler<TaskTitleChanged> {
    private static final Logger LOG = LogManager.getLogger(TaskTitleChangedEventHandler.class);
    private final TaskRepository repository;

    public TaskTitleChangedEventHandler(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(TaskTitleChanged event) {
        LOG.debug(() -> String.format("Received event: %s", event));
        Optional<Task> taskOptional = repository.findById(event.getStreamId().toString());
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setTitle(event.getTitle());
            repository.save(task);
        }
    }
}
