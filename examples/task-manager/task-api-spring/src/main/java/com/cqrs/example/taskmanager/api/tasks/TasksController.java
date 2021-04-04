package com.cqrs.example.taskmanager.api.tasks;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cqrs.commandbus.CommandDispatcher;
import com.cqrs.example.taskmanager.commands.AddDescriptionToTask;
import com.cqrs.example.taskmanager.commands.ChangeTaskDueDate;
import com.cqrs.example.taskmanager.commands.ChangeTaskTitle;
import com.cqrs.example.taskmanager.commands.CompleteTask;
import com.cqrs.example.taskmanager.commands.CreateTask;
import com.cqrs.example.taskmanager.domain.TaskNothingChangedException;
import com.cqrs.messaging.Command;
import com.cqrs.messaging.ID;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/tasks")
@EnableAutoConfiguration
public class TasksController {

    private final CommandDispatcher dispatcher;
    private final TaskRepository taskRepository;

    public TasksController(CommandDispatcher dispatcher, TaskRepository taskRepository) {
        this.dispatcher = dispatcher;
        this.taskRepository = taskRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Task>> getTasks() {
        List<Task> tasks = taskRepository.findAll(new Sort(DESC, "creationDate"));
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Task> createNewTask(@RequestBody Task body) {
        ID newTaskId = ID.fromObject(UUID.randomUUID());
        CreateTask create = new CreateTask(newTaskId, body.getTitle(), body.getDueDate());
        this.dispatcher.dispatch(create);

      //  triggerCommand(t -> Strings.isNotEmpty(t.getDescription()), t -> new AddDescriptionToTask(newTaskId, t.getDescription()), body);
      //  triggerCommand(t -> t.getCompletedAt() != null, t -> new CompleteTask(newTaskId, t.getCompletedAt()), body);

        Task response = new Task(newTaskId.toString(), body.getTitle(), body.getDescription(),
            body.getDueDate(), body.getCompletedAt(), Instant.now());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Task> updateTaskPartially(@PathVariable String id, @RequestBody Task body) {
        ID taskId = ID.fromObject(id);

        triggerCommand(t -> Strings.isNotEmpty(t.getTitle()), t -> new ChangeTaskTitle(taskId, t.getTitle()), body);
        triggerCommand(t -> Strings.isNotEmpty(t.getDescription()), t -> new AddDescriptionToTask(taskId, t.getDescription()), body);
        triggerCommand(t -> t.getDueDate() != null, t -> new ChangeTaskDueDate(taskId, t.getDueDate()), body);
        triggerCommand(t -> t.getCompletedAt() != null, t -> new CompleteTask(taskId, t.getCompletedAt()), body);

        Task response = new Task(taskId.toString(), body.getTitle(), body.getDescription(), body.getDueDate(),
            body.getCompletedAt(), Instant.now());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTaskById(@PathVariable String id) {
        //TODO
        //Implement command handler logic
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private <T extends Command> void triggerCommand(Function<Task, Boolean> predicate, Function<Task, T> commandFactory, Task body) {
        if (!predicate.apply(body)) {
            return;
        }
        try {
            T command = commandFactory.apply(body);
            this.dispatcher.dispatch(command);
        } catch (TaskNothingChangedException ignored) {
            // If nothing changed we consider this as perfectly fine
        }
    }
}
