package com.cqrs.example.taskmanager.domain;

import com.cqrs.domain.Repository;
import com.cqrs.example.taskmanager.commands.CreateTask;
import com.cqrs.messaging.CommandHandler;

public class CreateTaskCommandHandler implements CommandHandler<CreateTask> {

    private final Repository<Task> repository;

    public CreateTaskCommandHandler(Repository<Task> repository) {
        this.repository = repository;
    }

    @Override
    public void handle(CreateTask command) {

        Task task = new Task(command.getId(), command.getTitle(), command.getDueDate());

        this.repository.save(task, 0);
    }
}
