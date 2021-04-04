package com.cqrs.example.taskmanager.consumer.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cqrs.example.taskmanager.consumer.task.TaskRepository;
import com.cqrs.example.taskmanager.consumer.task.eventhandlers.TaskCompletedEventHandler;
import com.cqrs.example.taskmanager.consumer.task.eventhandlers.TaskCreatedEventHandler;
import com.cqrs.example.taskmanager.consumer.task.eventhandlers.TaskDescriptionAddedEventHandler;
import com.cqrs.example.taskmanager.consumer.task.eventhandlers.TaskDueDateChangedEventHandler;
import com.cqrs.example.taskmanager.consumer.task.eventhandlers.TaskTitleChangedEventHandler;
import com.cqrs.messaging.ActionHandlerResolver;

@Configuration
public class ActionHandlerResolverConfiguration {

    private final TaskRepository taskRepository;

    public ActionHandlerResolverConfiguration(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Bean
    public ActionHandlerResolver actionHandlerResolver() {
        ActionHandlerResolver resolver = new ActionHandlerResolver();
        resolver.registerActionHandler(
            new TaskCreatedEventHandler(taskRepository),
            new TaskTitleChangedEventHandler(taskRepository),
            new TaskDescriptionAddedEventHandler(taskRepository),
            new TaskDueDateChangedEventHandler(taskRepository),
            new TaskCompletedEventHandler(taskRepository)
        );

        return resolver;
    }
}
