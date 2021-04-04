package com.cqrs.example.taskmanager.domain;

import com.cqrs.domain.Repository;
import com.cqrs.example.taskmanager.commands.ChangeTaskDueDate;
import com.cqrs.messaging.CommandHandler;

public class ChangeTaskDueDateCommandHandler implements CommandHandler<ChangeTaskDueDate> {

    private final Repository<Task> repository;

    public ChangeTaskDueDateCommandHandler(Repository<Task> repository) {
        this.repository = repository;
    }

    @Override
    public void handle(ChangeTaskDueDate command) {
        Task task = this.repository.getById(command.getId());
        task.changeDueDate(command.getDueDate());

        this.repository.save(task, task.getVersion());
    }
}
