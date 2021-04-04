package com.cqrs.example.taskmanager.domain;

import com.cqrs.domain.Repository;
import com.cqrs.example.taskmanager.commands.AddDescriptionToTask;
import com.cqrs.messaging.CommandHandler;

public class AddDescriptionToTaskCommandHandler implements CommandHandler<AddDescriptionToTask> {

    private final Repository<Task> repository;

    public AddDescriptionToTaskCommandHandler(Repository<Task> repository) {
        this.repository = repository;
    }

    @Override
    public void handle(AddDescriptionToTask command) {

        Task task = this.repository.getById(command.getId());
        task.addDescription(command.getDescription());

        this.repository.save(task, task.getVersion());
    }
}
