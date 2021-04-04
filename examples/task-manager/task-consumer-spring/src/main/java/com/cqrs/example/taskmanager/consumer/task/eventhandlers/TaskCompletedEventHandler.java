package com.cqrs.example.taskmanager.consumer.task.eventhandlers;


import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cqrs.example.taskmanager.consumer.task.Task;
import com.cqrs.example.taskmanager.consumer.task.TaskRepository;
import com.cqrs.example.taskmanager.events.TaskCompleted;
import com.cqrs.messaging.EventHandler;

public class TaskCompletedEventHandler implements EventHandler<TaskCompleted> {

    private static final Logger LOG = LogManager.getLogger(TaskCompletedEventHandler.class);

    private final TaskRepository repository;

    public TaskCompletedEventHandler(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(TaskCompleted event) {
        LOG.debug(() -> String.format("Received event: %s", event));
        Optional<Task> taskOptional = repository.findById(event.getStreamId().toString());
        if (taskOptional.isPresent()) {
            Task task= taskOptional.get();
            task.setCompletedAt(event.getCompletedAt());
            repository.save(task);
        }
    }
}
