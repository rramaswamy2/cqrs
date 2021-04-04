package com.cqrs.example.taskmanager.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.cqrs.domain.AggregateNotFoundException;
import com.cqrs.example.taskmanager.domain.TaskException;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(AggregateNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(AggregateNotFoundException aggregateNotFoundException) {
        return new ResponseEntity<>(
            new ErrorResponse(
                new Error(HttpStatus.NOT_FOUND.toString(),
                    "Aggregate not found exception", aggregateNotFoundException.getMessage())),
            HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TaskException.class)
    public ResponseEntity<ErrorResponse> invalidRequest(TaskException exception) {
        return new ResponseEntity<>(
            new ErrorResponse(new Error(HttpStatus.NOT_FOUND.toString(),
                "Task exception", exception.getMessage())),
            HttpStatus.BAD_REQUEST);
    }


}
