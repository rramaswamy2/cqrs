package com.cqrs.example.taskmanager.domain;

import com.cqrs.domain.Repository;
import com.cqrs.example.taskmanager.commands.ChangeTaskTitle;
import com.cqrs.messaging.CommandHandler;

public class ChangeTaskTitleCommandHandler implements CommandHandler<ChangeTaskTitle> {

    private final Repository<Task> repository;

    public ChangeTaskTitleCommandHandler(Repository<Task> repository) {
        this.repository = repository;
    }

    @Override
    public void handle(ChangeTaskTitle command) {
        Task task = this.repository.getById(command.getId());
        task.changeTitle(command.getTitle());

        this.repository.save(task, task.getVersion());
    }
}
