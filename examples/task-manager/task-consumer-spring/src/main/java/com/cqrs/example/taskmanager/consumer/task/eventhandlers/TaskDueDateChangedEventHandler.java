package com.cqrs.example.taskmanager.consumer.task.eventhandlers;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cqrs.example.taskmanager.consumer.task.Task;
import com.cqrs.example.taskmanager.consumer.task.TaskRepository;
import com.cqrs.example.taskmanager.events.TaskDueDateChanged;
import com.cqrs.messaging.EventHandler;

public class TaskDueDateChangedEventHandler implements EventHandler<TaskDueDateChanged> {
    private static final Logger LOG = LogManager.getLogger(TaskDueDateChangedEventHandler.class);
    private final TaskRepository repository;

    public TaskDueDateChangedEventHandler(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(TaskDueDateChanged event) {
        LOG.debug(() -> String.format("Received event: %s", event));
        Optional<Task> taskOptional = repository.findById(event.getStreamId().toString());
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setDueDate(event.getDueDate());
            repository.save(task);
        }
    }
}
