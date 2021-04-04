package com.cqrs.example.taskmanager.api.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cqrs.commandbus.CommandDispatcher;
import com.cqrs.commandbus.local.SimpleCommandDispatcher;
import com.cqrs.domain.Repository;
import com.cqrs.example.taskmanager.domain.AddDescriptionToTaskCommandHandler;
import com.cqrs.example.taskmanager.domain.ChangeTaskDueDateCommandHandler;
import com.cqrs.example.taskmanager.domain.ChangeTaskTitleCommandHandler;
import com.cqrs.example.taskmanager.domain.CompleteTaskCommandHandler;
import com.cqrs.example.taskmanager.domain.CreateTaskCommandHandler;
import com.cqrs.example.taskmanager.domain.Task;
import com.cqrs.messaging.ActionHandlerResolver;

@Configuration
public class CommandDispatcherConfiguration {

    private Repository<Task> repository;

    public CommandDispatcherConfiguration(Repository<Task> repository) {
        this.repository = repository;
    }

    @Bean
    public CommandDispatcher dispatcher() {

        ActionHandlerResolver commandResolver = new ActionHandlerResolver();
        commandResolver.registerActionHandler(new AddDescriptionToTaskCommandHandler(repository));
        commandResolver.registerActionHandler(new ChangeTaskDueDateCommandHandler(repository));
        commandResolver.registerActionHandler(new ChangeTaskTitleCommandHandler(repository));
        commandResolver.registerActionHandler(new CompleteTaskCommandHandler(repository));
        commandResolver.registerActionHandler(new CreateTaskCommandHandler(repository));

        return new SimpleCommandDispatcher(commandResolver);
    }


}
