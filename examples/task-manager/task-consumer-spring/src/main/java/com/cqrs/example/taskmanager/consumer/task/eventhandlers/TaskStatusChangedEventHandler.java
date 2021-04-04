package com.cqrs.example.taskmanager.consumer.task.eventhandlers;

import java.util.Optional;

import com.cqrs.example.taskmanager.consumer.task.Task;
import com.cqrs.example.taskmanager.events.TaskStatusChanged;
import com.cqrs.example.taskmanager.events.TaskTitleChanged;
import com.cqrs.messaging.EventHandler;

