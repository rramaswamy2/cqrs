package com.cqrs.example.taskmanager.api.task;

import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.cqrs.commandbus.CommandDispatcher;
import com.cqrs.example.taskmanager.api.Application;
import com.cqrs.example.taskmanager.api.tasks.Task;
import com.cqrs.example.taskmanager.api.tasks.TaskRepository;
import com.cqrs.example.taskmanager.commands.AddDescriptionToTask;
import com.cqrs.example.taskmanager.commands.CompleteTask;
import com.cqrs.example.taskmanager.commands.CreateTask;
import com.cqrs.messaging.Command;
import com.cqrs.messaging.ID;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = Application.class
)
public class TaskRestControllerTest {

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        prepareInitialData();
    }

    private List<Task> expectedTasks;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CommandDispatcher dispatcher;

    public TaskRestControllerTest() {
    }

    private void prepareInitialData() {
        List<Command> commands = new ArrayList<>();
        expectedTasks = new ArrayList<>();

        commands.addAll(createTask(1, "Build Awesome Software", "ABCD"));
        commands.addAll(createTask(2, "Automate superb Infrastructure", "EFGH"));
        commands.addAll(createTask(3, "Deploy great Software on amazing Infrastructure", "IJKL"));

        commands.forEach(command -> dispatcher.dispatch(command));
        // await for last event to be received (and therefore be processed by the eventhandlers)
    }

    private ID createId(int id) {
        return ID.fromObject(String.format("%03d", id));
    }

    private List<Command> createTask(int id, String title, String description) {
        Instant creationDate = Instant.now().minus(Period.ofDays(id));
        Instant dueDate = Instant.now().plus(Period.ofDays(id));
        Instant completionAt = id < 2 ? Instant.now().plus(Period.ofDays(id)) : null;

        ID taskId = createId(id);
        List<Command> commands = new ArrayList<>();
        commands.add(new CreateTask(taskId, title, dueDate));
        commands.add(new AddDescriptionToTask(taskId, description));
        expectedTasks.add(new Task(taskId.toString(), title, description, creationDate, dueDate, completionAt));

        if (completionAt != null) {
            commands.add(new CompleteTask(taskId, completionAt));
        }
        return commands;
    }

    @Test
    public void givenTasks_whenGetTaskById_thenReturnJsonMap() throws Exception {
        mvc.perform(get("/task/{id}", "001").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isMap())
            .andExpect(jsonPath("$.title", is(expectedTasks.get(0).getTitle())))
            .andExpect(jsonPath("$.description", is(expectedTasks.get(0).getDescription())))
            .andExpect(jsonPath("$.creationDate", is(ISO_INSTANT.format(expectedTasks.get(0).getCreationDate()))))
            .andExpect(jsonPath("$.dueDate", is(ISO_INSTANT.format(expectedTasks.get(0).getDueDate()))))
            .andExpect(jsonPath("$.completedAt", is(ISO_INSTANT.format(expectedTasks.get(0).getCompletedAt()))));
    }

    @Test
    public void givenTasks_whenGetTasks_thenReturnJsonArray() throws Exception {
        mvc.perform(get("/task")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].title", is(expectedTasks.get(0).getTitle())))
            .andExpect(jsonPath("$[0].description", is(expectedTasks.get(0).getDescription())))
            .andExpect(jsonPath("$[0].creationDate", is(ISO_INSTANT.format(expectedTasks.get(0).getCreationDate()))))
            .andExpect(jsonPath("$[0].dueDate", is(ISO_INSTANT.format(expectedTasks.get(0).getDueDate()))))
            .andExpect(jsonPath("$[0].completedAt", is(ISO_INSTANT.format(expectedTasks.get(0).getCompletedAt()))))
            .andExpect(jsonPath("$[1].title", is(expectedTasks.get(1).getTitle())))
            .andExpect(jsonPath("$[1].description", is(expectedTasks.get(1).getDescription())))
            .andExpect(jsonPath("$[1].creationDate", is(ISO_INSTANT.format(expectedTasks.get(1).getCreationDate()))))
            .andExpect(jsonPath("$[1].dueDate", is(ISO_INSTANT.format(expectedTasks.get(1).getDueDate()))))
            .andExpect(jsonPath("$[1].completedAt", is(nullValue())))
            .andExpect(jsonPath("$[2].title", is(expectedTasks.get(2).getTitle())))
            .andExpect(jsonPath("$[2].description", is(expectedTasks.get(2).getDescription())))
            .andExpect(jsonPath("$[2].creationDate", is(ISO_INSTANT.format(expectedTasks.get(2).getCreationDate()))))
            .andExpect(jsonPath("$[2].dueDate", is(ISO_INSTANT.format(expectedTasks.get(2).getDueDate()))))
            .andExpect(jsonPath("$[2].completedAt", is(nullValue())));
    }

    @Test
    public void givenTasks_whenCreateTask_thenReturnCreatedTask() throws Exception {
        Instant instant = Instant.now();

        String newTask = String.format("{\"title\": \"Whohoo\", \"dueDate\": \"%s\"}",
            instant.plus(Period.ofDays(7)));
        mvc.perform(post("/task").contentType(MediaType.APPLICATION_JSON).content(newTask))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$").isMap())
            .andExpect(jsonPath("$.id", is(notNullValue())))
            .andExpect(jsonPath("$.title", is("Whohoo")))
            .andExpect(jsonPath("$.dueDate", is(ISO_INSTANT.format(instant.plus(Period.ofDays(7))))))
            .andExpect(jsonPath("$.completedAt", is(nullValue())));
    }

    @Test
    public void givenTasks_whenAddingDescription_thenReturnNothing() throws Exception {
        ID id = createId(1);
        String updateTaskDescription = "{\"description\": \"Task description added\"}";
        mvc.perform(patch("/task/{id}", id.toString()).contentType(MediaType.APPLICATION_JSON).content(
            updateTaskDescription))
            .andExpect(status().isOk());
    }

    @Test
    public void givenTasks_whenChangingTitle_thenReturnNothing() throws Exception {
        ID id = createId(2);
        String updateTaskTitle = "{\"title\": \"Title updated\"}";
        mvc.perform(patch("/task/{id}", id.toString()).contentType(MediaType.APPLICATION_JSON).content(
            updateTaskTitle))
            .andExpect(status().isOk());
    }

    @Test
    public void givenTasks_whenChangingDueDate_thenReturnNothing() throws Exception {
        ID id = createId(3);
        String updateDueDate =
            String.format("{\"dueDate\": \"%s\"}", Instant.now().plus(Period.ofDays(8)));
        mvc.perform(patch("/task/{id}", id.toString()).contentType(MediaType.APPLICATION_JSON).content(
            updateDueDate))
            .andExpect(status().isOk());
    }

    @Test
    public void givenTasks_whenCompletingTask_thenReturnNothing() throws Exception {
        ID id = createId(2);
        String updateCompletedTask =
            String.format("{\"completedAt\": \"%s\"}", Instant.now().plus(Period.ofDays(4)));
        mvc.perform(patch("/task/{id}", id.toString()).contentType(MediaType.APPLICATION_JSON).content(
            updateCompletedTask))
            .andExpect(status().isOk());
    }

    @Test
    public void givenTasks_whenDeleteTask_thenReturnNothing() throws Exception {
        ID id = createId(3);
        mvc.perform(delete("/task/{id}", id.toString()))
            .andExpect(status().isIAmATeapot());
    }
}
