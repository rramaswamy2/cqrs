package com.cqrs.example.taskmanager.domain;

import com.cqrs.domain.Repository;
import com.cqrs.example.taskmanager.commands.CompleteTask;
import com.cqrs.messaging.CommandHandler;

public class CompleteTaskCommandHandler implements CommandHandler<CompleteTask> {

    private final Repository<Task> repository;

    public CompleteTaskCommandHandler(Repository<Task> repository) {
        this.repository = repository;
    }

    @Override
    public void handle(CompleteTask command) {

        Task task = this.repository.getById(command.getId());
        task.complete(command.getCompletedAt());

        this.repository.save(task, task.getVersion());
    }
}
