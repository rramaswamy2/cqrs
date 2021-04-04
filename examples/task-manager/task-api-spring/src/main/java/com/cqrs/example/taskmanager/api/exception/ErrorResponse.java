package com.cqrs.example.taskmanager.api.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ErrorResponse {

    private final List<Error> errors = new ArrayList<>();

    public ErrorResponse(Error... errors) {
        for (Error error : errors) {
            this.errors.add(error);
        }
    }

    public ErrorResponse(List<Error> errors) {
        this.errors.addAll(errors);
    }

    public List<Error> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }

    public void addErrors(List<Error> errors) {
        this.errors.addAll(errors);
    }
}
